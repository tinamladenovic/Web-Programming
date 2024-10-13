<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 500px; margin-top: -50px;">
     
      <form @submit.prevent="submitFactory" class="factory-form">
        <div class="row mb-3">
          <label for="factoryName" class="col-sm-3 col-form-label text-start">Factory Name:</label>
          <div class="col-sm-9">
            <input type="text" id="factoryName" v-model="factory.name" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="factoryLocation" class="col-sm-3 col-form-label text-start">Location:</label>
          <div class="col-sm-9">
            <select id="factoryLocation" v-model="selectedLocation" @change="updateFactoryLocation" class="form-control" required>
              <option v-for="location in locations" :key="location.locationId" :value="location.locationId">
                {{ location.address }}
              </option>
            </select>
            <button @click="navigateToAddLocation" type="button" class="btn btn-outline-secondary mt-2">Add Location</button>
          </div>
        </div>

        <div class="row mb-3">
          <label for="manager" class="col-sm-3 col-form-label text-start">Manager:</label>
          <div class="col-sm-9">
            <select id="manager" v-model="factory.managerId" class="form-control" required>
              <option v-for="manager in availableManagers" :key="manager.id" :value="manager.id">
                {{ manager.username }}
              </option>
            </select>
            <button v-if="availableManagers.length === 0" @click="navigateToAddManager" type="button" class="btn btn-outline-secondary mt-2">Add Manager</button>
          </div>
        </div>

        <div class="row mb-3">
          <label for="openingTime" class="col-sm-3 col-form-label text-start">Opening Time:</label>
          <div class="col-sm-9">
            <input type="time" id="openingTime" v-model="factory.openingTime" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="closingTime" class="col-sm-3 col-form-label text-start">Closing Time:</label>
          <div class="col-sm-9">
            <input type="time" id="closingTime" v-model="factory.closingTime" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="logoName" class="col-sm-3 col-form-label text-start">Logo Name:</label>
          <div class="col-sm-9">
            <input type="text" id="logoName" v-model="factory.logo" class="form-control" required>
          </div>
        </div>

        <div class="row">
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">ADD CHOCOLATE FACTORY</button>
          </div>
        </div>
      </form>
      <p v-if="errorMessage" class="text-danger text-center mt-3">{{ errorMessage }}</p>
      <p v-if="successMessage" class="text-success text-center mt-3">{{ successMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const factory = ref({
  name: '',
  location: {
    address: '',
    latitude: null,
    longitude: null
  },
  openingTime: '',
  closingTime: '',
  logo: '',
  managerId: null,
  chocolates: [],
  rating: 0,
  status: 'OPEN'
});

const locations = ref([]);
const selectedLocation = ref(null);
const availableManagers = ref([]);
const successMessage = ref('');
const errorMessage = ref('');

const loadLocations = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/locations');
    const locationsData = response.data;
    locations.value = Object.values(locationsData);
  } catch (error) {
    console.error('Error loading locations:', error);
    errorMessage.value = 'Failed to load locations.';
  }
};

const loadAvailableManagers = async () => {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/users/managers');
    availableManagers.value = response.data.filter(manager => !manager.assignedFactoryId || manager.assignedFactoryId === '');
  } catch (error) {
    console.error('Error loading managers:', error);
    errorMessage.value = 'Failed to load managers.';
  }
};

const updateFactoryLocation = () => {
  const location = locations.value.find(loc => loc.locationId === selectedLocation.value);
  if (location) {
    factory.value.location = location;
  }
};

const submitFactory = async () => {
  try {
    const endpoint = 'http://localhost:8080/WebShopAppREST/rest/chocolateFactoryObjects';
    const response = await axios.post(endpoint, factory.value, { withCredentials: true });
    successMessage.value = 'Factory created successfully!';
    errorMessage.value = '';
    console.log('Factory created:', response.data);
    router.push('/');
  } catch (error) {
    console.error('Error creating factory:', error.message);
    errorMessage.value = 'Failed to create factory.';
  }
};

const navigateToAddLocation = () => {
  router.push('/addlocation');
};

const navigateToAddManager = () => {
  router.push('/addmanager');
};

onMounted(() => {
  loadLocations();
  loadAvailableManagers();
});
</script>

<style scoped>
.d-flex {
  min-height: 75vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 30px;
  background-color: #f0f8ff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.form-label {
  font-weight: normal;
}

/* Stilizacija za inpute, select, i dugmad */
input[type="text"],
input[type="time"],
select,
button {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
input[type="time"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

button[type="submit"] {
  border: 1px solid #007bff;
  background-color: transparent;
  color: #007bff;
  transition: background-color 0.3s ease, color 0.3s ease;
}

button[type="submit"]:hover {
  background-color: #007bff;
  color: white;
}

.text-danger {
  margin-top: 15px;
}

.text-success {
  margin-top: 15px;
}
</style>
