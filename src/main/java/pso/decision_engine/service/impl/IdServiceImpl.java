package pso.decision_engine.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import pso.decision_engine.service.IdService;

@Service
public class IdServiceImpl implements IdService {

	private DateTimeFormatter ldtformatter = DateTimeFormatter.ofPattern("yyMMddHHmmssms");

	@Override
	public String createShortUniqueId() {
		return LocalDateTime.now().format(ldtformatter)+Math.round(Math.random()*100d);
	}

}
