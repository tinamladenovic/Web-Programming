<template>
  <div class="user-container">
    <div class="filter-row">
      <!-- Polja za pretragu -->
      <div class="form-group">
        <label for="searchFirstName">Search by First Name: </label>
        <input type="text" id="searchFirstName" v-model="searchFirstName" class="form-control half-width">
      </div>
      <div class="form-group">
        <label for="searchLastName">Search by Last Name: </label>
        <input type="text" id="searchLastName" v-model="searchLastName" class="form-control half-width">
      </div>
      <div class="form-group">
        <label for="searchUsername">Search by Username: </label>
        <input type="text" id="searchUsername" v-model="searchUsername" class="form-control half-width">
      </div>
      <div class="form-group">
        <label for="roleFilter">Filter by Role: </label>
        <select id="roleFilter" v-model="roleFilter" class="form-control half-width">
          <option value="">All</option>
          <option value="ADMIN">Admin</option>
          <option value="CUSTOMER">Customer</option>
          <option value="WORKER">Worker</option>
          <option value="MANAGER">Manager</option>
        </select>
      </div>
      <div class="form-group">
        <label for="userTypeFilter">Filter by User Type: </label>
        <select id="userTypeFilter" v-model="userTypeFilter" class="form-control half-width">
          <option value="">All</option>
          <option value="NO_TYPE">No Type</option>
          <option value="BRONZE">Bronze</option>
          <option value="SILVER">Silver</option>
          <option value="GOLD">Gold</option>
        </select>
      </div>
    </div>
    
    <!-- Tabela korisnika -->
    <table class="table table-bordered mt-4">
      <thead>
        <tr>
          <th @click="sortBy('firstName')">First Name <span v-if="sortKey === 'firstName'">{{ sortOrders.firstName > 0 ? '▲' : '▼' }}</span></th>
          <th @click="sortBy('lastName')">Last Name <span v-if="sortKey === 'lastName'">{{ sortOrders.lastName > 0 ? '▲' : '▼' }}</span></th>
          <th @click="sortBy('username')">Username <span v-if="sortKey === 'username'">{{ sortOrders.username > 0 ? '▲' : '▼' }}</span></th>
          <th @click="sortBy('role')">Role <span v-if="sortKey === 'role'">{{ sortOrders.role > 0 ? '▲' : '▼' }}</span></th>
          <th @click="sortBy('customerType')">User Type <span v-if="sortKey === 'customerType'">{{ sortOrders.customerType > 0 ? '▲' : '▼' }}</span></th>
          <th @click="sortBy('earnedPoints')">Earned Points <span v-if="sortKey === 'earnedPoints'">{{ sortOrders.earnedPoints > 0 ? '▲' : '▼' }}</span></th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in sortedAndFilteredUsers" :key="user.id" :class="{'table-danger': isSuspicious(user)}">
          <td>{{ user.firstName }}</td>
          <td>{{ user.lastName }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.role }}</td>
          <td>{{ user.customerType.name }}</td>
          <td>{{ user.earnedPoints }}</td>
          <td>
            <!-- Dugme BLOCK vidljivo samo za korisnike koji nisu ADMIN i nisu već blokirani -->
            <button v-if="user.role !== 'ADMIN' && !user.blocked" @click="blockUser(user)" class="btn btn-danger btn-sm">Block</button>
            <span v-if="user.blocked" class="text-danger">Blocked</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted, computed } from 'vue';

export default {
  setup() {
    const users = ref([]);
    const purchases = ref([]);
    const searchFirstName = ref('');
    const searchLastName = ref('');
    const searchUsername = ref('');
    const roleFilter = ref('');
    const userTypeFilter = ref('');
    const sortKey = ref('username');
    const sortOrders = ref({
      firstName: 1,
      lastName: 1,
      username: 1,
      role: 1,
      customerType: 1,
      earnedPoints: 1
    });

    // Fetch all users and purchases
    const fetchUsersAndPurchases = async () => {
      try {
        const usersResponse = await axios.get('http://localhost:8080/WebShopAppREST/rest/users', {
          withCredentials: true,
        });
        users.value = Object.values(usersResponse.data);

        const purchasesResponse = await axios.get('http://localhost:8080/WebShopAppREST/rest/purchases', {
          withCredentials: true,
        });
        purchases.value = purchasesResponse.data;
      } catch (error) {
        console.error('Error fetching users or purchases:', error);
      }
    };

    onMounted(() => {
      fetchUsersAndPurchases();
    });

    // Prebroj koliko puta je korisnik otkazao kupovinu
    const getCancelCount = (username) => {
      return purchases.value.filter(purchase => {
        return purchase.customerName === username && purchase.status === 'CANCELLED';
      }).length;
    };

    // Funkcija koja proverava da li je korisnik sumnjiv
    const isSuspicious = (user) => {
      const cancelCount = getCancelCount(user.username);
      return cancelCount >= 5;
    };

    const sortedAndFilteredUsers = computed(() => {
      return users.value
        .filter(user => {
          const firstNameMatch = user.firstName?.toLowerCase().includes(searchFirstName.value.toLowerCase());
          const lastNameMatch = user.lastName?.toLowerCase().includes(searchLastName.value.toLowerCase());
          const usernameMatch = user.username?.toLowerCase().includes(searchUsername.value.toLowerCase());
          const roleMatch = !roleFilter.value || user.role === roleFilter.value;
          const userTypeMatch = !userTypeFilter.value || user.customerType.name === userTypeFilter.value;

          return firstNameMatch && lastNameMatch && usernameMatch && roleMatch && userTypeMatch;
        })
        .sort((a, b) => {
          const modifier = sortOrders.value[sortKey.value];
          if (sortKey.value === 'earnedPoints') {
            return modifier * (a[sortKey.value] - b[sortKey.value]);
          } else if (typeof a[sortKey.value] === 'string' && typeof b[sortKey.value] === 'string') {
            return modifier * a[sortKey.value].localeCompare(b[sortKey.value]);
          }
          return 0;
        });
    });

    const sortBy = key => {
      sortKey.value = key;
      sortOrders.value[key] = sortOrders.value[key] * -1;
    };

    // Funkcija za blokiranje korisnika
    const blockUser = async (user) => {
      try {
        user.blocked = true; // Ažuriraj lokalno stanje

        // API zahtev za blokiranje korisnika
        await axios.put(`http://localhost:8080/WebShopAppREST/rest/users/${user.id}/block`, {
          blocked: true
        }, {
          withCredentials: true,
        });

        console.log(`User ${user.username} has been blocked.`);
      } catch (error) {
        console.error('Error blocking user:', error);
      }
    };

    return {
      sortedAndFilteredUsers,
      searchFirstName,
      searchLastName,
      searchUsername,
      roleFilter,
      userTypeFilter,
      sortBy,
      sortKey,
      sortOrders,
      blockUser,
      isSuspicious // Dodaj funkciju u povratnu vrednost
    };
  },
};
</script>

<style scoped>
.user-container {
  padding: 20px;
  border-radius: 8px;
  margin-top: 20px;
  background-color: #e9f5ff;
}

h2.blue-label {
  color: #007bff;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 20px;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 200px;
}

input[type="text"],
select {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  padding: 10px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  border: 1px solid #ddd;
  padding: 8px;
}

.table th {
  background-color: #f5e7f5;
  text-align: left;
  cursor: pointer;
  color: #333;
}

/* Crveni red za sumnjive korisnike */
.table-danger {
  background-color: #f8d7da !important;
  color: #721c24 !important;
}

button {
  border: 1px solid #007bff;
  background-color: transparent;
  color: #007bff;
  font-size: 1.1rem;
  border-radius: 8px;
  transition: background-color 0.3s ease, color 0.3s ease;
}

button:hover {
  background-color: #007bff;
  color: white;
}
</style>
