package telran.employees;

import java.util.*;

//So far we do consider optimization
public class CompanyMapsImpl implements Company {
	TreeMap<Long, Employee> employees = new TreeMap<>();
	HashMap<String, List<Employee>> employeesDepartment = new HashMap<>();
	TreeMap<Float, List<Manager>> factorManagers = new TreeMap<>();

	@Override
	public Iterator<Employee> iterator() {
		List<Employee> employeeList = new ArrayList<>();
		employees.entrySet().forEach(e -> employeeList.add(e.getValue()));
		return employeeList.iterator();
	}

	@Override
	public void addEmployee(Employee empl) {
		long id = empl.getId();
		if (employees.get(id) != null) {
			throw new IllegalStateException("Employee is already registered");
		}
		employees.put(id, empl);
		employeesDepartment.computeIfAbsent(empl.getDepartment(), k -> new ArrayList<>()).add(empl);
		if (empl instanceof Manager) {
			addManager(empl);
		}
	}

	private void addManager(Employee empl) {
		Manager manager = (Manager) empl;
		factorManagers.computeIfAbsent(manager.factor, k -> new ArrayList<>()).add(manager);

	}

	@Override
	public Employee getEmployee(long id) {
		return employees.get(id);
	}

	@Override
	public Employee removeEmployee(long id) {
		Employee empl = employees.get(id);
		if (empl == null) {
			throw new NoSuchElementException();
		}
		employeesDepartmentClean(id);
		if (empl instanceof Manager) {
			factorManagersClean(empl);
		}
		return employees.remove(id);

	}

	private void factorManagersClean(Employee empl) {
		Manager manager = (Manager) empl;
		if(factorManagers.get(manager.factor).size() == 1) {
			factorManagers.remove(manager.factor);
		}
	}

	private void employeesDepartmentClean(long id) {
		String employeeDepartment = employees.get(id).getDepartment();
		if(employeesDepartment.get(employeeDepartment).size() == 1) {
			employeesDepartment.remove(employeeDepartment);
		}
	}

	@Override
	public int getDepartmentBudget(String department) {
		int result = 0;
		if(employeesDepartment.get(department) != null) {
	     result = employeesDepartment.get(department).stream()
					.mapToInt(Employee::computeSalary).sum();
		}

		return  result;
	}

	@Override
	public String[] getDepartments() {
		return employeesDepartment.keySet().stream().sorted().toArray(String[]::new);
	}

	@Override
	public Manager[] getManagersWithMostFactor() {
		Float key = factorManagers.lastKey();
		return factorManagers.get(key).toArray(Manager[]::new);
	}

}
