/**
 * UserServiceTestSuite.java
 */
package hu.bme.aut.tomeesample.service;


import org.junit.runners.Parameterized.*;
import org.junit.runners.*;
import org.junit.runner.*;

import static org.junit.Assert.assertEquals;
import hu.bme.aut.tomeesample.model.*;
import static org.mockito.Mockito.*;
import javax.persistence.*;
import org.apache.log4j.*;
import java.util.*;
import org.junit.*;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class UserServiceTestSuite {

	private static Logger logger;
	private UserService userService;
	private User user;
	
	public UserServiceTestSuite(UserService service, User user){
		this.userService = service;
		this.user = user;
	}
	
	@Parameters
	public static Collection<Object[]> dataSource(){
		Object[][] data = new Object[][]{
				{new UserService(),
				new User("cvirtue", "cvirtue", "cvirtue@wm.cvf.com", new Role("administrator"))
		}};
		return Arrays.asList(data);
	}
	
	@BeforeClass
	public static void setupLogger(){
		logger = Logger.getLogger(UserServiceTestSuite.class);
	}
	
	@Before
	public void setup(){
		userService.em = mock(EntityManager.class);
	}
	
	
	@Test
	public void testCase1(){
		logger.info("TestCase 1: user persist...");
		
		userService.create(user);
		logger.info("user: "+user.getUsername()+" is persisted...");
		
		verify(userService.em, times(1)).persist(user);
		logger.info("TestCase 1: user creation succeeded.");		
	}
	
	@Test
	public void testCase2(){
		logger.info("TestCase 2: user update...");
		
		userService.create(user);
		verify(userService.em, times(1)).persist(user);
		logger.info("user: "+user.getUsername()+" is persisted...");
		
		String newPassword = "cv11";
		user.setPassword(newPassword);
		userService.update(user);
		logger.info("user password changed to "+newPassword);
		verify(userService.em, times(1)).merge(user);
		
		logger.info("TestCase 2: user update succeeded.");
	}
	
	@Test
	public void testCase3(){
		logger.info("TestCase 3: user remove...");
		
		userService.create(user);
		logger.info("user: "+user.getUsername()+" is persisted...");
		verify(userService.em, times(1)).persist(user);
		
		userService.remove(user);
		logger.info("user: "+user.getUsername()+" is removed...");
		verify(userService.em, times(1)).remove(user);
		
		logger.info("TestCase 3: user remove succeeded.");		
	}
	
	@Test
	public void testCase4(){
		logger.info("TestCase 4: user findAll...");
		List<User> users = userService.findAll();
		verify(userService.em, times(1)).createNamedQuery("User.findAll", User.class);
		logger.info("TestCase 4: user findAll succeeded.");
	}
	
	@Test
	public void testCase5(){
		logger.info("TestCase 5: user findById...");
		User user = userService.findById(11l);
		verify(userService.em, times(1)).createNamedQuery("User.findById", User.class);
		logger.info("TestCase 5: user findById succeeded.");
	}
	
	@Test
	public void testCase6(){
		logger.info("TestCase 6: user findByName...");
		User user = userService.findByName("cvirtue");
		verify(userService.em, times(1)).createNamedQuery("User.findByName", User.class);
		logger.info("TestCase 6: user findByName succeeded.");
	}
}
