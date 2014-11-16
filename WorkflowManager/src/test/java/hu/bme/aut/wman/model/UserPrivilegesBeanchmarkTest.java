/**
 * UserPrivilegesBeanchmarkTest.java
 */
package hu.bme.aut.wman.model;

import static org.junit.Assert.fail;
import hu.bme.aut.wman.benchmark.Benchmarkable;
import hu.bme.aut.wman.benchmark.SimpleBenchmarker;
import hu.bme.aut.wman.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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
			@SuppressWarnings("deprecation")
			public void setup() {
				this.user = new User();
				Domain d = new Domain("System");
				Role admin = new Role("System Administrator", d);
				Privilege createUser = new Privilege("Create User");
				Privilege visitPage = new Privilege("Visit Page");
				Privilege removeRole = new Privilege("Remove Role");
				
				admin.addPrivilege(createUser);
				admin.addPrivilege(visitPage);
				admin.addPrivilege(removeRole);
				
				this.user.addDomainAssignment(new DomainAssignment(this.user, d, admin));
			}
			
			@Override
			protected void doExecute() throws Exception {
				if(hasPrivilege(this.user, "Simple Privilege", "System"))
					throw new Exception();
			}

			@Override
			public double exeute() throws Exception {
				return super.execute();
			}
			
			private boolean hasPrivilege(User user, final String privilege, final String domain) {
				DomainAssignment da = new ArrayList<DomainAssignment>(Collections2.filter(user.getDomainAssignments(), new Predicate<DomainAssignment>() {
					
					@Override
					public boolean apply(DomainAssignment da) {
						return da.getDomain().getName().equals(domain);
					}
				})).get(0);
				
				Collection<Role> roles = Collections2.filter(da.getUserRoles(), new Predicate<Role>() {
					
					@Override
					public boolean apply(Role role) {
						return role.hasPrivilege(privilege);
					}
				});
				return (roles.size() > 0);
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
			e.printStackTrace();
			fail();
		}
	}
	
	private Benchmarkable engine;
}
