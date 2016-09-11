package com.asiainfo.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.bo.Logs;
import com.asiainfo.bo.Recipient;
import com.asiainfo.redis.RedisConstants;
import com.asiainfo.util.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/checkin")
public class CheckinController {

	@Autowired
	private StringRedisTemplate template;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private ObjectMapper mapper;

	@RequestMapping("/saveCode")
	@ResponseBody
	public String saveCode(final String codes) {
		// 获取当天日期
		String date = TimeUtil.thisSDate();
		boolean isMember = template.opsForSet().isMember(RedisConstants.KEY_DATE, date);
		if (!isMember) {
			template.opsForSet().add(RedisConstants.KEY_DATE, date);
		}
		// 获取当前提交的logs id
		long logsId = template.opsForValue().increment(RedisConstants.LOGS_ID_INCR, 1);
		template.opsForList().rightPush(date, Long.toString(logsId));

		// 当前登录用户
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = user.getUsername();

		Logs logs = new Logs();
		logs.setLogId(logsId);
		logs.setUsername(username);
		logs.setCreateDate(new Date());
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(logs);
		} catch (JsonProcessingException e) {

		}
		template.opsForValue().set(RedisConstants.KEY_LOGS + logsId, jsonString);

		String[] logss = codes.split("\\n");
		for (String log : logss) {
			if (log.trim().length() != 0) {
				template.opsForList().rightPush(Long.toString(logsId), log.trim());
			}
		}
		return "SUCCESS";
	}

	@RequestMapping("/saveScript")
	@ResponseBody
	public String saveScript(final String scripts) {
		// 获取当天日期
		String date = TimeUtil.thisSDate();
		boolean isMember = template.opsForSet().isMember(RedisConstants.KEY_DATE, date);
		if (!isMember) {
			template.opsForSet().add(RedisConstants.KEY_DATE, date);
		}
		// 获取当前提交的logs id
		long logsId = template.opsForValue().increment(RedisConstants.LOGS_ID_INCR, 1);
		template.opsForList().rightPush(date, Long.toString(logsId));

		String[] logss = scripts.split("\\n");
		for (String log : logss) {
			if (log.trim().length() != 0) {
				template.opsForList().rightPush(Long.toString(logsId), log.trim());
			}
		}
		return "SUCCESS";
	}

	@RequestMapping("/logs/{date}")
	@ResponseBody
	public Set<String> listLogs(@PathVariable String date) {

		Locale locale = new Locale("zh");
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG, locale);

		Set<String> logss = new LinkedHashSet<String>();
		logss.add("// 以'//'或者'--'开头的行，邮件内容会忽略.#数字表示第几次重复.\n");
		Map<String, Integer> map = new HashMap<>();
		boolean isMember = template.opsForSet().isMember(RedisConstants.KEY_DATE, date);
		if (isMember) {
			List<String> logIds = template.opsForList().range(date, 0, -1);
			if (!logIds.isEmpty()) {
				for (String logId : logIds) {

					String logsJsong = template.opsForValue().get(RedisConstants.KEY_LOGS + logId);
					try {
						Logs logs = mapper.readValue(logsJsong, Logs.class);
						String username = logs.getUsername();
						Date createDate = logs.getCreateDate();
						logss.add("-- " + username + " / " + dateFormat.format(createDate));
					} catch (IOException e) {
						e.printStackTrace();
					}

					List<String> logs = template.opsForList().range(logId, 0, -1);
					if (!logs.isEmpty()) {
						for (String log : logs) {
							if (logss.contains(log)) {
								int i = map.get(log) == null ? 0 : map.get(log);
								map.put(log, ++i);
								log = "// " + log + " #" + i;
							}
							logss.add(log);
						}
					}
				}
			}
		}
		return logss;
	}

	@RequestMapping("/mail")
	@ResponseBody
	public String sendMail(final Recipient recipient) {

		MimeMessagePreparator preparator = new MimeMessagePreparator() {

			public void prepare(MimeMessage mimeMessage) throws Exception {
				if (recipient.isToAll()) {
					List<String> mailToList = template.opsForList().range(RedisConstants.MAIL_TO_LIST, 0, -1);
					final InternetAddress[] addresses = new InternetAddress[0];
					Set<InternetAddress> addressSet = new HashSet<InternetAddress>();
					for (String mailTo : mailToList) {
						InternetAddress address = new InternetAddress(mailTo);
						addressSet.add(address);
					}
					mimeMessage.setRecipients(Message.RecipientType.TO, addressSet.toArray(addresses));
				} else {
					// 收件人
					mimeMessage.setRecipients(Message.RecipientType.TO, recipient.to());
				}
				if (recipient.cc().length > 0) {
					// 抄送
					mimeMessage.setRecipients(Message.RecipientType.CC, recipient.cc());
				}
				if (recipient.bcc().length > 0) {
					// 密送
					mimeMessage.setRecipients(Message.RecipientType.BCC, recipient.bcc());
				}
				mimeMessage.setFrom(new InternetAddress("holmesycl@163.com"));
				mimeMessage.setSubject(recipient.getSubject());

				String text = recipient.getText();
				String[] logss = text.split("\\n");
				StringBuilder builder = new StringBuilder();
				for (String log : logss) {
					if (log.trim().length() != 0) {

						if (!log.startsWith("//") && !log.startsWith("--")) {
							builder.append(log.trim() + "\n");
						}
					}
				}
				mimeMessage.setText(builder.toString());
			}
		};
		mailSender.send(preparator);

		return "SUCCESS";
	}
}
