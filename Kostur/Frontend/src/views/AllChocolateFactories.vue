<template>
  <div class="d-flex justify-content-center align-items-center vh-100">
    <!-- Sekcija za pretragu i tabele bez plave linije -->
    <div class="search-table-container">
      <form class="mb-4 p-3">
        <!-- Sve pretrage u jednom redu -->
        <div class="filter-row">
          <div class="form-group">
            <label for="searchChocolate">Search by Choco Name:</label>
            <input v-model="searchChocolate" @input="filterFactories" id="searchChocolate" name="searchChocolate" placeholder="Search by Chocolate Name" class="form-control" type="text" />
          </div>
          <div class="form-group">
            <label for="searchName">Search by Factory Name:</label>
            <input v-model="searchName" @input="filterFactories" id="searchName" name="searchName" placeholder="Search by Factory Name" class="form-control" type="text" />
          </div>
          <div class="form-group">
            <label for="searchLocation">Search by Location:</label>
            <input v-model="searchLocation" @input="filterFactories" id="searchLocation" name="searchLocation" placeholder="Search by Location" class="form-control" type="text" />
          </div>
          <div class="form-group">
            <label for="searchRating">Search by Rating:</label>
            <input v-model="searchRating" @input="filterFactories" id="searchRating" name="searchRating" type="number" step="0.1" placeholder="Search by Rating" class="form-control" />
          </div>
          <div class="form-group">
            <label for="searchChocolateType">Chocolate Type:</label>
            <select v-model="searchChocolateType" @change="filterFactories" id="searchChocolateType" name="searchChocolateType" class="form-select">
              <option value="">All Types</option>
              <option v-for="type in chocolateTypes" :key="type">{{ type }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="searchChocolateKind">Chocolate Kind:</label>
            <select v-model="searchChocolateKind" @change="filterFactories" id="searchChocolateKind" name="searchChocolateKind" class="form-select">
              <option value="">All Kinds</option>
              <option v-for="kind in chocolateKinds" :key="kind">{{ kind }}</option>
            </select>
          </div>
          <div class="form-group">
            <label for="openFactoriesOnly">Show Only Open Factories:</label>
            <input type="checkbox" v-model="openFactoriesOnly" @change="filterFactories" id="openFactoriesOnly" name="openFactoriesOnly" class="form-check-input" />
          </div>
        </div>
      </form>

      <!-- Sortirajuća tabela -->
      <table class="table table-bordered text-center">
        <thead>
          <tr>
            <th>Logo</th>
            <th @click="sortBy('name')">Factory Name <span v-if="sortKey === 'name'">{{ sortOrders.name }}</span></th>
            <th @click="sortBy('location')">Location <span v-if="sortKey === 'location'">{{ sortOrders.location }}</span></th>
            <th @click="sortBy('rating')">Rating <span v-if="sortKey === 'rating'">{{ sortOrders.rating }}</span></th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="factory in filteredFactories" :key="factory.id">
            <td><img :src="getFactoryLogo(factory.logo)" alt="Logo" class="factory-logo" /></td>
            <td>{{ factory.name }}</td>
            <td>{{ factory.location.address }}</td>
            <td>{{ factory.rating }}</td>
            <td>
              <button @click="viewFactoryProfile(factory.factoryId)" class="btn btn-outline-primary">VIEW</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, watch } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const factories = ref([]);
const chocolates = ref([]);
const searchName = ref('');
const searchChocolate = ref('');
const searchLocation = ref('');
const searchRating = ref('');
const searchChocolateType = ref('');
const searchChocolateKind = ref('');
const openFactoriesOnly = ref(false);

const chocolateTypes = ref(['PLAIN', 'FOR_COOKING', 'FOR_DRINKING']);
const chocolateKinds = ref(['DARK', 'MILK', 'WHITE']);

const sortKey = ref('');
const sortOrders = ref({
  name: 1,
  location: 1,
  rating: 1
});

const loadFactories = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/chocolateFactoryObjects');
    factories.value = response.data;
  } catch (error) {
    console.error("Error loading factories:", error);
  }
};

const loadChocolates = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/chocolates');
    chocolates.value = response.data;
  } catch (error) {
    console.error("Error loading chocolates:", error);
  }
};

onMounted(() => {
  loadFactories();
  loadChocolates();
});

const isFactoryOpen = (factory) => factory.status === 'OPEN';

const sortedFactories = computed(() => {
  return factories.value
    .slice()
    .sort((a, b) => {
      if (a.status === 'OPEN' && b.status !== 'OPEN') return -1;
      if (a.status !== 'OPEN' && b.status === 'OPEN') return 1;
      return 0;
    })
    .sort((a, b) => {
      if (sortKey.value === 'name') {
        return sortOrders.value.name * a.name.localeCompare(b.name);
      } else if (sortKey.value === 'location') {
        return sortOrders.value.location * a.location.address.localeCompare(b.location.address);
      } else if (sortKey.value === 'rating') {
        return sortOrders.value.rating * (a.rating - b.rating);
      }
      return 0;
    });
});

const filteredFactories = computed(() => {
  return sortedFactories.value.filter(factory => {
    const matchesName = factory.name.toLowerCase().includes(searchName.value.toLowerCase());
    const matchesLocation = factory.location.address.toLowerCase().includes(searchLocation.value.toLowerCase());
    const matchesRating = searchRating.value === '' || factory.rating >= parseFloat(searchRating.value);
    const isOpenFactory = !openFactoriesOnly.value || isFactoryOpen(factory);

    const matchesChocolate = searchChocolate.value === '' || chocolates.value.some(chocolate =>
      chocolate.name.toLowerCase().includes(searchChocolate.value.toLowerCase()) && chocolate.factoryId === factory.id
    );
    const matchesChocolateType = !searchChocolateType.value || chocolates.value.some(chocolate =>
      chocolate.type === searchChocolateType.value && chocolate.factoryId === factory.id
    );
    const matchesChocolateKind = !searchChocolateKind.value || chocolates.value.some(chocolate =>
      chocolate.kind === searchChocolateKind.value && chocolate.factoryId === factory.id
    );

    return matchesName && matchesLocation && matchesRating && isOpenFactory &&
           matchesChocolate && matchesChocolateType && matchesChocolateKind;
  });
});

const getFactoryLogo = (logoFileName) => `/images/${logoFileName}`;

const viewFactoryProfile = (factoryId) => {
  if (factoryId) {
    router.push({ name: 'factoryprofile', params: { id: factoryId } });
  }
};

const sortBy = key => {
  sortKey.value = key;
  sortOrders.value[key] *= -1;
  filteredFactories.value.sort((a, b) => {
    const modifier = sortOrders.value[key];
    if (key === 'name' || key === 'location') {
      const aValue = key === 'location' ? a.location.address.toLowerCase() : a[key].toLowerCase();
      const bValue = key === 'location' ? b.location.address.toLowerCase() : b[key].toLowerCase();
      return modifier * aValue.localeCompare(bValue);
    } else {
      return modifier * (a[key] - b[key]);
    }
  });
};

watch(filteredFactories, async () => {
  await nextTick();
});
</script>

<style scoped>
.d-flex {
  min-height: 100vh;
}

/* Sekcija pretrage i tabele bez plave linije */
.search-table-container {
  padding: 20px;
  background-color: #e9f5ff; /* Svetloplava pozadina */
}

.filter-row {
  display: flex;
  flex-wrap: wrap; /* Omogućava da se pretrage preliju u novi red ako nema dovoljno prostora */
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 20px;
}

.form-group {
  flex-basis: 12%; /* Svako polje zauzima oko 12% širine kako bi više polja stalo u jedan red */
  display: flex;
  flex-direction: column;
  min-width: 150px; /* Osigurava minimalnu širinu da polja ne budu preuska */
}

input[type="text"],
input[type="number"],
select {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  padding: 10px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
input[type="number"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

.table {
  margin-top: 20px;
  width: 100%;
  border-collapse: collapse;
}

.table th,
.table td {
  border: 1px solid #e0e0e0;
  padding: 10px;
  text-align: center;
}

.table th {
  background-color: #f5e7f5; /* Svetla boja za zaglavlje tabele */
  font-weight: bold;
  cursor: pointer;
}

.factory-logo {
  width: 100px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
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
