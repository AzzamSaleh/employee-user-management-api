async function login() {
  const u = document.getElementById("username").value.trim();
  const p = document.getElementById("password").value;

  // store auth then verify with a low-cost, allowed call
  setAuth(u, p);

  // Verify credentials by touching an endpoint allowed for all business roles
  const ping = await raw("/employees", "GET");
  if (!ping.ok && ping.status !== 403 && ping.status !== 200) {
    // 401 or network => bad credentials
    document.getElementById("error").textContent = "Invalid credentials";
    localStorage.clear();
    return;
  }

  // Discover roles (ADMIN/MANAGER/EMPLOYEE) via probes
  await probeRoles();

  window.location.href = "index.html";
}

function hasRole(r) {
  const roles = JSON.parse(localStorage.getItem("roles") || "[]");
  return roles.includes("ROLE_" + r) || roles.includes(r);
}
