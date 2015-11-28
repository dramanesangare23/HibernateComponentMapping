import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ManageEmployee {
	private static SessionFactory factory;

	public static void main(String[] args) {
		try{ 
			factory = new Configuration().configure().buildSessionFactory(); 
		}catch (Throwable ex) { 
			System.err.println("Failed to create sessionFactory object." + ex); 
			throw new ExceptionInInitializerError(ex); 
		}
		ManageEmployee ME = new ManageEmployee(); 

		/* Let us have an address object */ 
		Address address1 = ME.addAddress("Kondapur","Hyderabad","AP","532");

		/* Add employee records in the database */ 
		Integer empID1 = ME.addEmployee("Manoj", "Kumar", 4000, address1); 

		/* Let us have another address object */
		Address address2 = ME.addAddress("Saharanpur","Ambehta","UP","111");

		/* Add another employee record in the database */ 
		Integer empID2 = ME.addEmployee("Dilip", "Kumar", 3000, address2); 

		/* List down all the employees */ 
		ME.listEmployees();

		/* Update employee's salary records */
		ME.updateEmployee(empID1, 5000);

		/* Delete an employee from the database */
		//		ME.deleteEmployee(empID2);

		/* List down all the employees */
		ME.listEmployees();

		factory.close();
	}

	/* Method to add an address records in the database*/
	public Address addAddress(String street, String city, String state, String zipcode){
		Session session = factory.openSession();
		Transaction transaction = null;
		Address address = null;
		Integer addressID = null;
		try {
			transaction = session.beginTransaction();
			address = new Address(street, city, state, zipcode);
			addressID = (Integer) session.save(address);
			transaction.commit();
		} catch (HibernateException e) {
			if(transaction != null)
				transaction.rollback();
			e.printStackTrace();
		} finally{
			session.close();
		}
		return address;
	}

	/* Method to add an employee record in the database */ 
	public Integer addEmployee(String fname, String lname, int salary, Address address){ 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		Integer employeeID = null;
		try{
			tx = session.beginTransaction(); 
			Employee employee = new Employee(fname, lname, salary, address); 
			employeeID = (Integer) session.save(employee); 
			tx.commit(); 
		}catch (HibernateException e) { 
			if (tx!=null) 
				tx.rollback(); 
			e.printStackTrace(); 
		}finally { 
			session.close(); 
		} 
		return employeeID; 
	}

	/* Method to list all the employees detail */ 
	public void listEmployees( ){ 
		Session session = factory.openSession();
		Transaction tx = null; 
		try{
			tx = session.beginTransaction();
			List<Employee> employees = session.createQuery("FROM Employee").list();
			for (Iterator<Employee> iterator1 = employees.iterator(); iterator1.hasNext();){ 
				Employee employee = (Employee) iterator1.next();
				System.out.println("---------------------------------------");
				System.out.print("First Name: " + employee.getFirstName());
				System.out.print(" Last Name: " + employee.getLastName()); 
				System.out.println(" Salary: " + employee.getSalary()); 
				Address add = employee.getAddress(); 
				System.out.println("Address");
				System.out.println("\tStreet: " + add.getStreet()); 
				System.out.println("\tCity: " + add.getCity()); 
				System.out.println("\tState: " + add.getState()); 
				System.out.println("\tZipcode: " + add.getZipcode());
			}
			tx.commit();
		}catch (HibernateException e) {
			if (tx!=null) 
				tx.rollback();
			e.printStackTrace();
		}finally { 
			session.close();
		}
	}

	/* Method to update salary for an employee */ 
	public void updateEmployee(Integer employeeID, int salary ){ 
		Session session = factory.openSession(); 
		Transaction tx = null; 
		try{ 
			tx = session.beginTransaction(); 
			Employee employee = (Employee)session.get(Employee.class, employeeID); 
			employee.setSalary( salary ); 
			session.update(employee); 
			tx.commit(); 
		}catch (HibernateException e) { 
			if (tx!=null) 
				tx.rollback(); 
			e.printStackTrace(); 
		}finally { 
			session.close(); 
		} 
	}
}
