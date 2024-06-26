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
		employees.values().forEach(e -> employeeList.add(e));
		return employeeList.iterator();
	}

	@Override
	public void addEmployee(Employee empl) {
		long id = empl.getId();
		if (employees.get(id) != null) {
			throw new IllegalStateException("Employee with ID provided is already registered");
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
		Employee result = employees.remove(id);
		if (result == null) {
			throw new NoSuchElementException();
		}
		removeEmployeeFromAllMaps(id, result);
		return result;

	}

	private void removeEmployeeFromAllMaps(long id, Employee empl) {
		String depKey = empl.getDepartment();
		removeFromIndexMap(employeesDepartment, depKey, empl);

		if (empl instanceof Manager) {
			Manager manager = (Manager) empl;
			float factorKey = manager.factor;
			removeFromIndexMap(factorManagers, factorKey, (Manager) empl);
		}
		employees.remove(id);
	}

	<K, V> void removeFromIndexMap(Map<K, List<V>> map, K key, V empl) {
		List<V> list = map.get(key);
		list.remove(empl);
		if (list.size() == 0) {
			map.remove(key);
		}
	}

	@Override
	public int getDepartmentBudget(String department) {
		int result = 0;

		if (employeesDepartment.get(department) != null) {
			result = employeesDepartment.get(department).stream().mapToInt(Employee::computeSalary).sum();
		}
		return result;
	}

	@Override
	public String[] getDepartments() {
		return employeesDepartment.keySet().stream().sorted().toArray(String[]::new);
	}

	@Override
	public Manager[] getManagersWithMostFactor() {
		Manager[] result = new Manager[0];
		if (factorManagers.size() > 0) {
			Float key = factorManagers.lastKey();
			result = factorManagers.get(key).toArray(Manager[]::new);
		}
		return result;
	}

}
