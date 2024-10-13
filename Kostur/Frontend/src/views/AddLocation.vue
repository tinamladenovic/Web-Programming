<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 500px; margin-top: -50px;">
      
      <form @submit.prevent="submitLocation" class="location-form">
        <div class="row mb-3">
          <label for="address" class="col-sm-3 col-form-label text-start">Address:</label>
          <div class="col-sm-9">
            <input type="text" id="address" v-model="location.address" class="form-control" autocomplete="street-address" required>
          </div>
        </div>

        <div class="row">
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">ADD LOCATION</button>
          </div>
        </div>
      </form>

      <p v-if="successMessage" class="text-success text-center mt-3">{{ successMessage }}</p>
      <p v-if="errorMessage" class="text-danger text-center mt-3">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

const location = ref({
  latitude: null,
  longitude: null,
  address: ''
});

const successMessage = ref('');
const errorMessage = ref('');

const submitLocation = async () => {
  try {
    const geocodeResponse = await axios.get('https://nominatim.openstreetmap.org/search', {
      params: {
        q: location.value.address,
        format: 'json',
        limit: 1
      }
    });

    if (geocodeResponse.data.length > 0) {
      const geocodeData = geocodeResponse.data[0];
      location.value.latitude = parseFloat(geocodeData.lat);
      location.value.longitude = parseFloat(geocodeData.lon);

      const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/locations', location.value);
      successMessage.value = 'Location added successfully!';
      errorMessage.value = '';
      router.push('/addfactory');
    } else {
      errorMessage.value = 'Failed to geocode the address.';
      successMessage.value = '';
    }
  } catch (error) {
    errorMessage.value = 'Failed to add location.';
    successMessage.value = '';
  }
};
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

h2 {
  color: #343a40;
  text-align: center;
  font-size: 2rem;
  font-weight: normal;
}

/* Stilizacija za labele */
.form-label {
  font-weight: normal;
}

/* Tanke linije za inpute i dugmad */
input[type="text"],
button[type="submit"] {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

/* Dugmad sa plavim outline stilom */
button[type="submit"] {
  border: 1px solid #007bff;
  background-color: transparent;
  color: #007bff;
  font-size: 1.1rem;
  transition: background-color 0.3s ease, color 0.3s ease;
}

button[type="submit"]:hover {
  background-color: #007bff;
  color: white;
}

/* Dodavanje margine ispod dugmeta */
button[type="submit"] {
  margin-top: 20px;
}

.text-success {
  margin-top: 15px;
  color: green;
}

.text-danger {
  margin-top: 15px;
  color: red;
}
</style>
