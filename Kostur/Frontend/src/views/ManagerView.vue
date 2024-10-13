<template>
  <div class="d-flex justify-content-center align-items-center vh-100">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 1500px;">
      <div class="manager-view">

        <!-- Sekcija za Chocolates -->
        <div class="section-container">
          <h4>CHOCOLATES</h4>
          <table class="table">
            <thead>
              <tr>
                <th>Logo</th>
                <th @click="sortChocolates('name')">Name</th>
                <th @click="sortChocolates('price')">Price ($)</th>
                <th @click="sortChocolates('type')">Type</th>
                <th @click="sortChocolates('kind')">Kind</th>
                <th @click="sortChocolates('weight')">Weight (g)</th>
                <th @click="sortChocolates('description')">Description</th>
                <th @click="sortChocolates('status')">Status</th>
                <th @click="sortChocolates('quantity')">Quantity</th>
                <th>Edit</th>
                <th>Delete</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="chocolate in sortedChocolates" :key="chocolate.id">
                <td><img :src="getChocolatePicture(chocolate.picture)" :alt="chocolate.name" class="chocolate-picture" width="50" height="50"/></td>
                <td>{{ chocolate.name }}</td>
                <td>{{ chocolate.price.toFixed(2) }}</td>
                <td>{{ chocolate.type }}</td>
                <td>{{ chocolate.kind }}</td>
                <td>{{ chocolate.weight }}</td>
                <td>{{ chocolate.description }}</td>
                <td>{{ chocolate.status }}</td>
                <td>{{ chocolate.quantity }}</td>
                <td>
                  <button @click="editChocolate(chocolate)" class="btn btn-outline-secondary">Edit</button>
                </td>
                <td>
                  <button @click="deleteChocolate(chocolate.id)" class="btn btn-outline-danger">Delete</button>
                </td>
              </tr>
            </tbody>
          </table>
          <div class="button-container">
            <button @click="navigateToAddChocolate" class="btn btn-outline-primary">ADD CHOCOLATE</button>
          </div>
        </div>

        <!-- Sekcija za Workers -->
        <div class="section-container">
          <h4>WORKERS</h4>
          <table class="table">
            <thead>
              <tr>
                <th>Username</th>
                <th>First Name</th>
                <th>Last Name</th>
                <th>Gender</th>
                <th>Birth Date</th>
                <th>Role</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="worker in workers" :key="worker.id">
                <td>{{ worker.username }}</td>
                <td>{{ worker.firstName }}</td>
                <td>{{ worker.lastName }}</td>
                <td>{{ worker.gender }}</td>
                <td>{{ formatDate(worker.birthDate) }}</td>
                <td>{{ worker.role }}</td>
              </tr>
            </tbody>
          </table>
          <div class="button-container">
            <button @click="navigateToAddWorker" class="btn btn-outline-primary">ADD WORKER</button>
          </div>
        </div>

        <!-- Sekcija za Orders -->
        <div class="section-container">
          <h4>ORDERS</h4>
          <form @submit.prevent="applyFilters" class="form-inline">
            <div class="form-group">
              <label for="minPrice">Min Price:</label>
              <input v-model.number="filters.minPrice" id="minPrice" type="number" step="0.01" placeholder="Min Price" class="form-control">
            </div>
            <div class="form-group">
              <label for="maxPrice">Max Price:</label>
              <input v-model.number="filters.maxPrice" id="maxPrice" type="number" step="0.01" placeholder="Max Price" class="form-control">
            </div>
            <div class="form-group">
              <label for="startDate">Start Date:</label>
              <input v-model="filters.startDate" id="startDate" type="date" class="form-control">
            </div>
            <div class="form-group">
              <label for="endDate">End Date:</label>
              <input v-model="filters.endDate" id="endDate" type="date" class="form-control">
            </div>
            <button type="submit" class="btn btn-primary w-100 mt-3">Apply Filters</button>
          </form>

          <div class="purchases" v-if="filteredPurchases.length">
            <table class="table">
              <thead>
                <tr>
                  <th @click="sortPurchases('id')">Order ID</th>
                  <th @click="sortPurchases('customerName')">Customer</th>
                  <th @click="sortPurchases('purchaseDateTime')">Date & Time</th>
                  <th @click="sortPurchases('price')">Total Amount</th>
                  <th @click="sortPurchases('status')">Status</th>
                  <th>Items</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="purchase in filteredPurchases" :key="purchase.id">
                  <td>{{ purchase.id }}</td>
                  <td>{{ purchase.customerName }}</td>
                  <td>{{ formatDate(purchase.purchaseDateTime) }}</td>
                  <td>{{ purchase.price.toFixed(2) }}</td>
                  <td>{{ purchase.status }}</td>
                  <td>
                    <ul>
                      <li v-for="item in purchase.chocolates" :key="item.id">{{ item.name }}</li>
                    </ul>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  setup() {
    const manager = ref(null);
    const factory = ref(null);
    const purchases = ref([]);
    const customers = ref([]);
    const workers = ref([]);
    const router = useRouter();
    
    const sortOrder = ref({
      field: null,
      ascending: true
    });

    const filters = ref({
      minPrice: null,
      maxPrice: null,
      startDate: null,
      endDate: null,
    });

    const sortedChocolates = computed(() => {
      if (!factory.value) return [];

      const sorted = [...factory.value.chocolates];
      if (sortOrder.value.field) {
        sorted.sort((a, b) => {
          const aValue = a[sortOrder.value.field];
          const bValue = b[sortOrder.value.field];

          if (aValue > bValue) return sortOrder.value.ascending ? 1 : -1;
          if (aValue < bValue) return sortOrder.value.ascending ? -1 : 1;
          return 0;
        });
      }
      return sorted;
    });

    const fetchManagerData = async () => {
      try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/currentUser', {
          withCredentials: true,
        });
        manager.value = response.data;

        const factoryResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactoryObjects/${manager.value.factoryId}`, {
          withCredentials: true,
        });
        factory.value = factoryResponse.data;

        const purchasesResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/factory/${manager.value.factoryId}`, {
          withCredentials: true,
        });
        purchases.value = purchasesResponse.data;

        const customersResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/customers/factory/${manager.value.factoryId}`, {
          withCredentials: true,
        });
        customers.value = customersResponse.data;

        const workersResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/users/workers/factory/${manager.value.factoryId}`, {
          withCredentials: true,
        });
        workers.value = workersResponse.data;

      } catch (error) {
        console.error('Error fetching manager data:', error);
      }
    };

    const getChocolatePicture = (pictureName) => {
      if (pictureName) {
        return `/images/${pictureName}`;
      } else {
        return '/images/default.png';
      }
    };

    const sortChocolates = (field) => {
      if (sortOrder.value.field === field) {
        sortOrder.value.ascending = !sortOrder.value.ascending;
      } else {
        sortOrder.value.field = field;
        sortOrder.value.ascending = true;
      }
    };

    const formatDate = (date) => {
      if (!date) return "";
      const formattedDate = new Date(date);
      return formattedDate.toLocaleDateString();
    };

    const filteredPurchases = computed(() => {
      return purchases.value.filter(purchase => {
        const matchesPrice = (!filters.value.minPrice || purchase.price >= filters.value.minPrice) &&
                             (!filters.value.maxPrice || purchase.price <= filters.value.maxPrice);
        const matchesDate = (!filters.value.startDate || new Date(purchase.purchaseDateTime) >= new Date(filters.value.startDate)) &&
                            (!filters.value.endDate || new Date(purchase.purchaseDateTime) <= new Date(filters.value.endDate));
        return matchesPrice && matchesDate;
      });
    });

    const sortPurchases = (field) => {
      if (sortOrder.value.field === field) {
        sortOrder.value.ascending = !sortOrder.value.ascending;
      } else {
        sortOrder.value.field = field;
        sortOrder.value.ascending = true;
      }

      filteredPurchases.value.sort((a, b) => {
        const aValue = a[field];
        const bValue = b[field];
        if (aValue > bValue) return sortOrder.value.ascending ? 1 : -1;
        if (aValue < bValue) return sortOrder.value.ascending ? -1 : 1;
        return 0;
      });
    };

    const applyFilters = () => {
      if (sortOrder.value.field) {
        sortPurchases(sortOrder.value.field);
      }
    };

    const navigateToAddWorker = () => {
      router.push('/addworker');
    };

    const navigateToAddChocolate = () => {
      router.push('/addchocolate');
    };

    const editChocolate = (chocolate) => {
      router.push(`/edit-chocolate/${chocolate.id}`);
    };

    const deleteChocolate = async (chocolateId) => {
      try {
        await axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${chocolateId}`, {
          withCredentials: true,
        });
        factory.value.chocolates = factory.value.chocolates.filter(chocolate => chocolate.id !== chocolateId);
      } catch (error) {
        console.error('Error deleting chocolate:', error);
      }
    };

    onMounted(() => {
      fetchManagerData();
    });

    return {
      manager,
      factory,
      purchases,
      customers,
      workers,
      filters,
      sortedChocolates,
      filteredPurchases,
      formatDate,
      sortChocolates,
      sortPurchases,
      applyFilters,
      navigateToAddWorker,
      navigateToAddChocolate,
      getChocolatePicture,
      editChocolate,
      deleteChocolate
    };
  },
};
</script>

<style scoped>
.d-flex {
  min-height: 100vh;
}

.card {
  padding: 30px;
  background-color: transparent; /* Transparentna pozadina kartice */
  box-shadow: none; /* Bez senki i okvira spolja */
}

h2 {
  color: #007bff;
  text-align: center;
  font-size: 2rem;
  font-weight: normal;
}

h3, h4 {
  color: #007bff;
  margin-bottom: 20px;
  text-align: center; /* Centriranje naslova */
  font-size: 2rem; /* Veća veličina fonta */
  font-weight: normal; /* Normalan font */
}

.form-inline {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 10px;
}

.form-group {
  display: flex;
  flex-direction: column;
  margin-right: 10px;
}

.form-control {
  border: 1px solid #007bff;
  border-radius: 8px;
  padding: 10px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-control:focus {
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

/* Razmak iznad i ispod dugmadi za dodavanje čokolade i radnika */
.button-container {
  margin-top: 20px;  /* Razmak iznad dugmeta */
  margin-bottom: 20px;  /* Razmak ispod dugmeta */
  text-align: center;  /* Centriranje dugmeta */
}

.table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
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
}

/* Stilovi za uokvirene sekcije */
.section-container {
  /* Uklonjen tamno plavi border */
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  background-color: #e9f5ff; /* Svetloplava pozadina */
}
</style>
