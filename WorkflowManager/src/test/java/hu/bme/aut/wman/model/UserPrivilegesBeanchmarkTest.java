/**
 * UserPrivilegesBeanchmarkTest.java
 */
package hu.bme.aut.wman.model;

import static org.junit.Assert.fail;
import hu.bme.aut.wman.benchmark.Benchmarkable;
import hu.bme.aut.wman.utils.StringUtils;

import java.util.Collection;

import hu.bme.aut.wman.benchmark.SimpleBenchmarker;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

import org.junit.Test;
/**
 * @author Imre Szekeres
 * @version "%I%, %G%"
 */
@RunWith(Parameterized.class)
public class UserPrivilegesBeanchmarkTest {

	public UserPrivilegesBeanchmarkTest(Benchmarkable engine) {
		this.engine = engine;
	}
	
	
	@Parameters
	public static Collection<Object[]> data() {
		Benchmarkable simple = new SimpleBenchmarker() {
			
			@Override
			public void setup() {
				this.user = new User();
				Role admin = new Role("admin");
				Privilege createUser = new Privilege("Create User");
				Privilege visitPage = new Privilege("Visit Page");
				Privilege removeRole = new Privilege("Remove Role");
				
				admin.addPrivilege(createUser);
				admin.addPrivilege(visitPage);
				admin.addPrivilege(removeRole);
				
				this.user.addRole(admin);
			}
			
			@Override
			protected void doExecute() throws Exception {
				if(user.hasPrivilege("Resignate Very Slowly"))
					throw new Exception();
			}

			@Override
			public double exeute() throws Exception {
				return super.execute();
			}
			
			private User user;
		};
		return Arrays.asList(new Object[][] {{ simple }});
	}
	
	@Test
	public void simpleBenchmark() {
		
		try {
			double result = engine.exeute();
			System.out.println(StringUtils.build("Average runtime: ", Double.toString(result), " ms"));
		} catch(Exception e) {
			fail();
		}
	}
	
	private Benchmarkable engine;
}
