package hu.bme.aut.wman.services.model;

import hu.bme.aut.wman.service.AbstractDataService;

public class TestDataService extends AbstractDataService<TestEntity> {

	@Override
	protected Class<TestEntity> getEntityClass() {
		return TestEntity.class;
	}

}
