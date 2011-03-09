package org.spockframework.springintegration.examples.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author janm
 */
@Service
public class SomeService {

	@Transactional
	public List<Integer> idolFind() {
		return Collections.emptyList();
	}

}
