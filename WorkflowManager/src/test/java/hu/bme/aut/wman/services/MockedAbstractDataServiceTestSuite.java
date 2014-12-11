/**
 * MockedPrivilegeServiceTestSuite.java
 */
package hu.bme.aut.wman.services;

import static org.mockito.Mockito.mock;
import hu.bme.aut.wman.model.AbstractEntity;
import hu.bme.aut.wman.service.AbstractDataService;
import hu.bme.aut.wman.services.model.TestDataService;
import hu.bme.aut.wman.services.model.TestEntity;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class MockedAbstractDataServiceTestSuite {

	private static final Logger LOGGER = Logger.getLogger( MockedAbstractDataServiceTestSuite.class );

	private AbstractDataService<TestEntity> service;
	private EntityManager entityManagerMock;

	private TestEntity spyEntity;

	public MockedAbstractDataServiceTestSuite() {
	}

	@Before
	public void initContext() {
		service = new TestDataService();
		entityManagerMock = mock(EntityManager.class);
		service.setEntityManager( entityManagerMock );

		TestEntity testEntity = new TestEntity("Test");
		spyEntity = Mockito.spy(testEntity);

		Mockito.when(entityManagerMock.merge(spyEntity)).thenReturn( spyEntity );
	}

	@Test
	public void testSaveNew() {
		try {
			service.save( spyEntity );
			Mockito.verify(entityManagerMock, Mockito.times(1)).persist( spyEntity );
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testSaveExist() {
		Mockito.when(spyEntity.getId()).thenReturn(new Long(1024));

		try {
			service.save( spyEntity );
			Mockito.verify(entityManagerMock, Mockito.times(1)).merge( spyEntity );
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testAttach() {
		try {
			Assert.assertEquals(spyEntity, service.attach( spyEntity ));
			Mockito.verify(entityManagerMock, Mockito.times(1)).merge( spyEntity );
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testDelete() {
		try {
			service.delete( spyEntity );
			Mockito.verify(entityManagerMock, Mockito.times(1)).remove( spyEntity );
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testSelectAll() {
		ArrayList<TestEntity> result = Lists.newArrayList(new TestEntity("A"), new TestEntity("B"), new TestEntity("C"), new TestEntity("D"));
		mockCriteriaQueryBuilder(result);

		try {
			Assert.assertEquals(result, service.selectAll());
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testSelectById() {
		Mockito.when(spyEntity.getId()).thenReturn(new Long(1024));
		ArrayList<TestEntity> result = Lists.newArrayList(spyEntity);
		mockCriteriaQueryBuilder(result);

		try {
			Assert.assertEquals(spyEntity, service.selectById(new Long(1024)));
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testCallNamedQuery() {
		TypedQuery<TestEntity> nqMock = Mockito.mock(TypedQuery.class);
		Mockito.when(entityManagerMock.createNamedQuery("NQ", TestEntity.class)).thenReturn(nqMock);

		ArrayList<Entry<String, Object>> parameterList = new ArrayList<Entry<String, Object>>();
		parameterList.add(new AbstractMap.SimpleEntry<String, Object>(AbstractEntity.PR_ID, new Long(1024)));

		try {
			service.callNamedQuery("NQ", parameterList);
			Mockito.verify(nqMock, Mockito.times(1)).setParameter(AbstractEntity.PR_ID,  new Long(1024));
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testIsNewWithNew() {
		try {
			Assert.assertTrue(service.isNew(spyEntity));
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testIsNewWithExist() {
		Mockito.when(spyEntity.getId()).thenReturn(new Long(1024));

		try {
			Assert.assertFalse(service.isNew(spyEntity));
		} catch(Exception e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	private void mockCriteriaQueryBuilder(TestEntity... resultElements) {
		mockCriteriaQueryBuilder(Lists.newArrayList(resultElements));
	}

	private void mockCriteriaQueryBuilder(List<TestEntity> resultElements) {
		CriteriaBuilder criteriaBuilderMock = Mockito.mock(CriteriaBuilder.class);
		Mockito.when(entityManagerMock.getCriteriaBuilder()).thenReturn(criteriaBuilderMock);

		CriteriaQuery<TestEntity> criteriaQueryMock = Mockito.mock(CriteriaQuery.class);
		Mockito.when(criteriaBuilderMock.createQuery(TestEntity.class)).thenReturn(criteriaQueryMock);

		Root<TestEntity> rootMock = Mockito.mock(Root.class);
		Mockito.when(criteriaQueryMock.from(TestEntity.class)).thenReturn(rootMock);
		Mockito.when(criteriaQueryMock.select(rootMock)).thenReturn(criteriaQueryMock);
		Mockito.when(criteriaQueryMock.where((Predicate[]) Mockito.any())).thenReturn(criteriaQueryMock);

		TypedQuery<TestEntity> typedQueryMock = Mockito.mock(TypedQuery.class);
		Mockito.when(entityManagerMock.createQuery(criteriaQueryMock)).thenReturn(typedQueryMock);
		Mockito.when(typedQueryMock.getResultList()).thenReturn(resultElements);
	}
}
