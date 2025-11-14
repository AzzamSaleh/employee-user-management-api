const tbody = document.querySelector("#empTable tbody");
const formDiv = document.getElementById("employeeForm");
const adminLink = document.getElementById("adminLink");
const msg = document.getElementById("msg");
const formTitle = document.getElementById("formTitle");
const cancelBtn = document.getElementById("cancelBtn");

let editingId = null;

window.onload = async () => {
  // If roles not present (e.g., direct navigation), probe them
  const roles = JSON.parse(localStorage.getItem("roles") || "[]");
  if (!roles.length) await probeRoles();

  // Gate admin link
  if (!hasRole("ADMIN")) adminLink.style.display = "none";

  // Show create/edit form only for MANAGER/ADMIN
  if (hasRole("MANAGER") || hasRole("ADMIN")) {
    formDiv.classList.remove("hidden");
  }

  await loadEmployees();
};

async function loadEmployees() {
  tbody.innerHTML = "";
  msg.textContent = "";
  try {
    const data = await apiRequest("/employees");
    data.forEach(e => addRow(e));
  } catch (err) {
    msg.textContent = err.message;
    msg.className = "error";
  }
}

function addRow(e) {
  const tr = document.createElement("tr");
  tr.innerHTML = `
    <td>${e.id}</td>
    <td>${e.firstName} ${e.lastName}</td>
    <td>${e.email}</td>
    <td class="row-actions" id="action-${e.id}"></td>`;
  tbody.appendChild(tr);

  const cell = document.getElementById(`action-${e.id}`);

  if (hasRole("MANAGER") || hasRole("ADMIN")) {
    const editBtn = document.createElement("button");
    editBtn.textContent = "Edit";
    editBtn.onclick = () => startEdit(e);
    cell.appendChild(editBtn);
  }
  if (hasRole("ADMIN")) {
    const delBtn = document.createElement("button");
    delBtn.textContent = "Delete";
    delBtn.onclick = () => deleteEmployee(e.id);
    cell.appendChild(delBtn);
  }
}

function startEdit(e) {
  editingId = e.id;
  document.getElementById("firstName").value = e.firstName;
  document.getElementById("lastName").value  = e.lastName;
  document.getElementById("email").value     = e.email;
  formTitle.textContent = `Edit Employee #${e.id}`;
  cancelBtn.classList.remove("hidden");
  msg.textContent = "";
  msg.className = "helper";
}

function resetForm() {
  editingId = null;
  document.getElementById("firstName").value = "";
  document.getElementById("lastName").value  = "";
  document.getElementById("email").value     = "";
  formTitle.textContent = "Add Employee";
  cancelBtn.classList.add("hidden");
  msg.textContent = "";
  msg.className = "helper";
}

async function saveEmployee() {
  const firstName = document.getElementById("firstName").value.trim();
  const lastName  = document.getElementById("lastName").value.trim();
  const email     = document.getElementById("email").value.trim();

  try {
    if (editingId == null) {
      // Create (MANAGER/ADMIN)
      await apiRequest("/employees", "POST", { firstName, lastName, email });
      msg.textContent = "Employee added.";
      msg.className = "success";
    } else {
      // Update (MANAGER/ADMIN)
      await apiRequest("/employees", "PUT", { id: editingId, firstName, lastName, email });
      msg.textContent = `Employee #${editingId} updated.`;
      msg.className = "success";
    }
    resetForm();
    await loadEmployees();
  } catch (err) {
    msg.textContent = err.message;
    msg.className = "error";
  }
}

async function deleteEmployee(id) {
  if (!confirm("Delete this employee?")) return;
  try {
    await apiRequest(`/employees/${id}`, "DELETE");
    await loadEmployees();
  } catch (err) {
    msg.textContent = err.message;
    msg.className = "error";
  }
}
