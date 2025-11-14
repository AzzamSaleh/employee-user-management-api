const tbodyU = document.querySelector("#userTable tbody");

window.onload = async () => {
  // Only ADMIN should see this page
  if (!hasRole("ADMIN")) return logout();
  await loadUsers();
};

async function loadUsers() {
  try {
    const data = await apiRequest("/users");
    tbodyU.innerHTML = "";
    data.forEach(u => {
      // u.roles may not be present depending on your backend DTO
      const roles = Array.isArray(u.roles) ? u.roles.join(", ") : "";
      tbodyU.insertAdjacentHTML(
        "beforeend",
        `<tr><td>${u.username}</td><td>${u.enabled}</td><td>${roles}</td></tr>`
      );
    });
  } catch (e) {
    tbodyU.innerHTML = `<tr><td colspan="3" class="error">${e.message}</td></tr>`;
  }
}

async function addUser() {
  const username = document.getElementById("uName").value.trim();
  const password = document.getElementById("uPass").value;
  const role = document.getElementById("uRole").value;

  try {
    await apiRequest(`/users?roles=${encodeURIComponent(role)}`, "POST", { username, password });
    document.getElementById("umsg").textContent = "User added!";
    await loadUsers();
  } catch (err) {
    document.getElementById("umsg").textContent = err.message;
    document.getElementById("umsg").className = "error";
  }
}
