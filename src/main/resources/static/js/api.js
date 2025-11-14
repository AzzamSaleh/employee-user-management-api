const API_BASE = "http://localhost:8081/api"; // backend port

function getAuth() { return localStorage.getItem("auth"); }
function setAuth(u, p) {
  localStorage.setItem("auth", "Basic " + btoa(u + ":" + p));
  localStorage.setItem("username", u);
}
function logout() { localStorage.clear(); window.location.href = "login.html"; }

async function apiRequest(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json", "Authorization": getAuth() };
  const options = { method, headers };
  if (body !== null) options.body = JSON.stringify(body);

  const res = await fetch(API_BASE + path, options);
  if (!res.ok) {
    if (res.status === 401) logout();
    const err = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(err.message || "Request failed");
  }
  // 204 no content
  if (res.status === 204) return null;
  return res.json();
}

// NEW: raw request that never throws (used for permission probing)
async function raw(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json", "Authorization": getAuth() };
  const options = { method, headers };
  if (body !== null) options.body = JSON.stringify(body);
  try {
    const res = await fetch(API_BASE + path, options);
    let json = null;
    try { json = await res.json(); } catch(_) {}
    return { ok: res.ok, status: res.status, json };
  } catch (e) {
    return { ok: false, status: 0, json: null };
  }
}

// NEW: discover roles by probing endpoints that map to your SecurityConfig
async function probeRoles() {
  const roles = new Set();

  // Anyone authenticated with EMPLOYEE/MANAGER/ADMIN can GET employees
  const rEmp = await raw("/employees", "GET");
  if (rEmp.ok) roles.add("ROLE_EMPLOYEE");

  // Only ADMIN can reach /api/users
  const rAdmin = await raw("/users", "GET");
  if (rAdmin.ok) roles.add("ROLE_ADMIN");

  // MANAGER or ADMIN can PUT /employees; we do a harmless probe that should be 400 for allowed roles
  const probeEmployee = { id: 0, firstName: "", lastName: "", email: "" }; // invalid on purpose
  const rMgr = await raw("/employees", "PUT", probeEmployee);
  if (rMgr.status === 400 || rMgr.status === 200) roles.add("ROLE_MANAGER"); // 400 validation means permission exists

  localStorage.setItem("roles", JSON.stringify(Array.from(roles)));
  return Array.from(roles);
}

function hasRole(r) {
  const roles = JSON.parse(localStorage.getItem("roles") || "[]");
  return roles.includes("ROLE_" + r) || roles.includes(r); // tolerate either form
}
