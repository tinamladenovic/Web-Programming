<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 1000px;">
      <div class="row">
        <!-- Profile Section -->
        <div class="col-md-6 user-profile-container section-container">
          <form @submit.prevent="updateProfile">
            <h4 class="text-center mb-3">PROFILE</h4>
            <div class="row mb-3">
              <label for="username" class="col-sm-4 col-form-label text-start">Username:</label>
              <div class="col-sm-8">
                <input type="text" v-model="user.username" id="username" class="form-control half-width" required />
              </div>
            </div>
            <div class="row mb-3">
              <label for="firstName" class="col-sm-4 col-form-label text-start">First Name:</label>
              <div class="col-sm-8">
                <input type="text" v-model="user.firstName" id="firstName" class="form-control half-width" required />
              </div>
            </div>
            <div class="row mb-3">
              <label for="lastName" class="col-sm-4 col-form-label text-start">Last Name:</label>
              <div class="col-sm-8">
                <input type="text" v-model="user.lastName" id="lastName" class="form-control half-width" required />
              </div>
            </div>
            <div class="row mb-3">
              <label for="password" class="col-sm-4 col-form-label text-start">Password:</label>
              <div class="col-sm-8">
                <input type="password" v-model="user.password" id="password" class="form-control half-width" required />
              </div>
            </div>
            <div class="row mb-3">
              <label for="gender" class="col-sm-4 col-form-label text-start">Gender:</label>
              <div class="col-sm-8">
                <select v-model="user.gender" id="gender" class="form-control half-width" required>
                  <option value="MALE">Male</option>
                  <option value="FEMALE">Female</option>
                </select>
              </div>
            </div>
            <div class="row mb-3">
              <label for="birthDate" class="col-sm-4 col-form-label text-start">Birth Date:</label>
              <div class="col-sm-8">
                <input type="date" v-model="user.birthDate" id="birthDate" class="form-control half-width" required />
              </div>
            </div>
            <div class="row">
              <div class="col-12">
                <button type="submit" class="btn btn-primary w-100">Update Profile</button>
              </div>
            </div>
            <p v-if="successMessage" class="text-success">{{ successMessage }}</p>
          </form>
        </div>

        <!-- Orders Section (visible only to customers) -->
        <div class="col-md-6 purchases-container section-container" v-if="user.role === 'CUSTOMER'">
          <h4 class="text-center mb-3">ORDERS</h4>
          <div class="row mb-2">
            <label for="factoryNameFilter" class="col-sm-3 col-form-label">Search by Factory Name:</label>
            <div class="col-sm-9">
              <input type="text" v-model="filters.factoryName" id="factoryNameFilter" class="form-control half-width" placeholder="Factory Name" />
            </div>
          </div>
          <div class="row mb-2">
            <label for="priceFrom" class="col-sm-3 col-form-label">Price From:</label>
            <div class="col-sm-9">
              <input type="number" v-model.number="filters.priceFrom" id="priceFrom" class="form-control half-width" placeholder="Price From" />
            </div>
          </div>
          <div class="row mb-2">
            <label for="priceTo" class="col-sm-3 col-form-label">Price To:</label>
            <div class="col-sm-9">
              <input type="number" v-model.number="filters.priceTo" id="priceTo" class="form-control half-width" placeholder="Price To" />
            </div>
          </div>
          <div class="row mb-2">
            <label for="dateFrom" class="col-sm-3 col-form-label">Date From:</label>
            <div class="col-sm-9">
              <input type="date" v-model="filters.dateFrom" id="dateFrom" class="form-control half-width" />
            </div>
          </div>
          <div class="row mb-2">
            <label for="dateTo" class="col-sm-3 col-form-label">Date To:</label>
            <div class="col-sm-9">
              <input type="date" v-model="filters.dateTo" id="dateTo" class="form-control half-width" />
            </div>
          </div>
          <div class="row mt-3 mb-3">
            <div class="col-12">
              <button @click="applyFilters" class="btn btn-outline-primary w-100">Apply Filters</button>
            </div>
          </div>

          <!-- Table of purchases -->
          <table class="table table-bordered mt-3">
            <thead>
              <tr>
                <th @click="sortPurchases('factoryName')">Factory Name</th>
                <th @click="sortPurchases('purchaseDateTime')">Date</th>
                <th @click="sortPurchases('price')">Price</th>
                <th>Products</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="purchase in filteredSortedPurchases" :key="purchase.id">
                <td>{{ getFactoryName(purchase) }}</td>
                <td>{{ purchase.purchaseDateTime }}</td>
                <td>{{ purchase.price }} RSD</td>
                <td>
                  <ul>
                    <li v-for="item in purchase.chocolates" :key="item.id">
                      {{ item.name }} - {{ item.quantity }} pcs
                    </li>
                  </ul>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted, computed } from 'vue';

export default {
  setup() {
    const user = ref({
      id: '',
      username: '',
      password: '',
      firstName: '',
      lastName: '',
      gender: '',
      birthDate: '',
      role: '',
      customerType: '',
      earnedPoints: 0,
      shoppingCart: ''
    });

    const purchases = ref([]);
    const filters = ref({
      factoryName: '',
      priceFrom: null,
      priceTo: null,
      dateFrom: '',
      dateTo: ''
    });
    const factories = ref({}); // Kreiramo mapu za fabrike
    const sortField = ref('purchaseDateTime');
    const sortOrder = ref('asc');
    const successMessage = ref('');

    const fetchUserProfile = async () => {
      try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/currentUser', {
          withCredentials: true
        });
        user.value = response.data;

        // Fetch purchases only if user is a customer
        if (user.value.role === 'CUSTOMER') {
          const fullName = `${user.value.firstName} ${user.value.lastName}`;
          const purchasesResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/${encodeURIComponent(fullName)}/purchases`, {
            withCredentials: true
          });
          purchases.value = purchasesResponse.data;

          // Fetch factory names for all purchases
          await fetchFactoryNames();
        }
      } catch (error) {
        console.error('Error fetching user profile or purchases:', error);
      }
    };

    const fetchFactoryNames = async () => {
      try {
        const factoryIds = [...new Set(purchases.value.map(purchase => purchase.factoryId))]; // Prikupi unikatne ID-jeve fabrika
        for (const factoryId of factoryIds) {
          const factoryResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactoryObjects/${factoryId}`, {
            withCredentials: true
          });
          factories.value[factoryId] = factoryResponse.data.name; // Sačuvaj ime fabrike pod odgovarajućim factoryId
        }
      } catch (error) {
        console.error('Error fetching factory names:', error);
      }
    };

    const getFactoryName = (purchase) => {
      return factories.value[purchase.factoryId] || 'N/A'; // Vraća ime fabrike na osnovu factoryId ili 'N/A' ako nije pronađeno
    };

    const filteredSortedPurchases = computed(() => {
      if (!Array.isArray(purchases.value)) {
        return [];
      }
      return purchases.value
        .filter(purchase => {
          const matchesFactoryName = filters.value.factoryName
            ? getFactoryName(purchase).toLowerCase().includes(filters.value.factoryName.toLowerCase())
            : true;
          const matchesPrice = (filters.value.priceFrom === null || purchase.price >= filters.value.priceFrom) &&
                               (filters.value.priceTo === null || purchase.price <= filters.value.priceTo);
          const matchesDateFrom = filters.value.dateFrom ? new Date(purchase.purchaseDateTime) >= new Date(filters.value.dateFrom) : true;
          const matchesDateTo = filters.value.dateTo ? new Date(purchase.purchaseDateTime) <= new Date(filters.value.dateTo) : true;
          return matchesFactoryName && matchesPrice && matchesDateFrom && matchesDateTo;
        })
        .sort((a, b) => {
          let comparison = 0;
          if (sortField.value === 'factoryName') {
            comparison = getFactoryName(a).localeCompare(getFactoryName(b));
          } else if (sortField.value === 'price') {
            comparison = a.price - b.price;
          } else if (sortField.value === 'purchaseDateTime') {
            comparison = new Date(a.purchaseDateTime) - new Date(b.purchaseDateTime);
          }
          return sortOrder.value === 'asc' ? comparison : -comparison;
        });
    });

    const applyFilters = () => {
      console.log('Filters applied');
    };

    const sortPurchases = (field) => {
      if (sortField.value === field) {
        sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
      } else {
        sortField.value = field;
        sortOrder.value = 'asc';
      }
    };

    const updateProfile = async () => {
      if (!user.value.username || !user.value.firstName || !user.value.lastName) {
        alert('Username, First Name, and Last Name are required.');
        return;
      }

      try {
        const response = await axios.put('http://localhost:8080/WebShopAppREST/rest/profile', user.value, {
          withCredentials: true,
          headers: {
            'Content-Type': 'application/json'
          }
        });
        successMessage.value = 'Profile updated successfully!';
        console.log(response.data);
      } catch (error) {
        console.error('Error updating profile:', error.response ? error.response.data : error.message);
        alert('Failed to update profile. Please try again.');
      }
    };

    onMounted(() => {
      fetchUserProfile();
    });

    return {
      user,
      purchases,
      filters,
      sortField,
      sortOrder,
      filteredSortedPurchases,
      getFactoryName,
      updateProfile,
      applyFilters,
      sortPurchases,
      successMessage
    };
  }
};
</script>


<style scoped>
.d-flex {
  min-height: 100vh;
}

.card {
  padding: 30px;
  background-color: transparent;
  box-shadow: none;
}

h2, h4 {
  color: #007bff;
  text-align: center;
  font-size: 2rem;
  font-weight: normal;
}

.form-label {
  font-weight: normal;
}

.half-width {
  width: 50%;
}

input[type="text"],
input[type="password"],
input[type="date"],
input[type="number"],
select,
button {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  padding: 10px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
input[type="password"]:focus,
input[type="date"]:focus,
input[type="number"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
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

button[type="submit"] {
  margin-top: 20px;
}

button.btn-outline-primary {
  margin-top: 10px;
  margin-bottom: 10px;
}

.table-bordered {
  border-collapse: collapse;
  width: 100%;
}

.table-bordered th,
.table-bordered td {
  border: 1px solid #ddd;
  padding: 8px;
}

.table-bordered th {
  background-color: #f5e7f5;
  text-align: center;
}

.purchases-container .row.mb-3 {
  margin-bottom: 5px;
}

.user-profile-container,
.purchases-container,
.section-container {
  padding: 20px;
  border-radius: 8px;
  background-color: #e9f5ff;
  margin-top: 10px;
}

p {
  padding: 10px;
  background-color: #f8f8f8;
  border: 1px solid #ddd;
  border-radius: 5px;
  color: #333;
}

.section-container {
  margin-bottom: 10px;
}

.card {
  background-color: transparent;
  box-shadow: none;
  border: none;
  padding: 0;
}
</style>
