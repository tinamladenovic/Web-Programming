<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 500px; margin-top: -50px;">
      
      <form @submit.prevent="createWorker" class="worker-form">
        <div class="row mb-3">
          <label for="firstName" class="col-sm-3 col-form-label text-start">First Name:</label>
          <div class="col-sm-9">
            <input type="text" id="firstName" v-model="newWorker.firstName" class="form-control" autocomplete="given-name" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="lastName" class="col-sm-3 col-form-label text-start">Last Name:</label>
          <div class="col-sm-9">
            <input type="text" id="lastName" v-model="newWorker.lastName" class="form-control" autocomplete="family-name" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="username" class="col-sm-3 col-form-label text-start">Username:</label>
          <div class="col-sm-9">
            <input type="text" id="username" v-model="newWorker.username" class="form-control" autocomplete="username" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="password" class="col-sm-3 col-form-label text-start">Password:</label>
          <div class="col-sm-9">
            <input type="password" id="password" v-model="newWorker.password" class="form-control" autocomplete="new-password" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="gender" class="col-sm-3 col-form-label text-start">Gender:</label>
          <div class="col-sm-9">
            <select id="gender" v-model="newWorker.gender" class="form-control" required>
              <option value="MALE">MALE</option>
              <option value="FEMALE">FEMALE</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="birthDate" class="col-sm-3 col-form-label text-start">Birth Date:</label>
          <div class="col-sm-9">
            <input type="date" id="birthDate" v-model="newWorker.birthDate" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="role" class="col-sm-3 col-form-label text-start">Role:</label>
          <div class="col-sm-9">
            <select id="role" v-model="newWorker.role" class="form-control" required>
              <option value="WORKER">WORKER</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="customerType" class="col-sm-3 col-form-label text-start">Customer Type:</label>
          <div class="col-sm-9">
            <select id="customerType" v-model="newWorker.customerType" class="form-control" required>
              <option value="NO_TYPE">NO_TYPE</option>
              <option value="BRONZE">BRONZE</option>
              <option value="SILVER">SILVER</option>
              <option value="GOLD">GOLD</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="earnedPoints" class="col-sm-3 col-form-label text-start">Earned Points:</label>
          <div class="col-sm-9">
            <input type="number" id="earnedPoints" v-model="newWorker.earnedPoints" class="form-control" required>
          </div>
        </div>

        <div class="row">
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">ADD WORKER</button>
          </div>
        </div>
      </form>

      <p v-if="errorMessage" class="text-danger text-center mt-3">{{ errorMessage }}</p>
      <p v-if="successMessage" class="text-success text-center mt-3">{{ successMessage }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { useRouter, useRoute } from 'vue-router';

export default {
  data() {
    return {
      newWorker: {
        firstName: '',
        lastName: '',
        username: '',
        password: '',
        gender: 'MALE',
        birthDate: '',
        role: 'WORKER',
        customerType: 'NO_TYPE',
        earnedPoints: 0,
        shoppingCart: null,
        blocked: false,
        factoryId: null 
      },
      errorMessage: '',
      successMessage: ''
    };
  },
  created() {
    const route = useRoute();
    this.newWorker.factoryId = route.params.factoryId;
  },
  methods: {
    async createWorker() {
      try {
        const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/users/createuser', this.newWorker);
        this.successMessage = 'Worker created successfully!';
        this.errorMessage = '';
        this.clearForm();
      } catch (error) {
        this.successMessage = '';
        if (error.response && error.response.data && error.response.data.message) {
          this.errorMessage = error.response.data.message;
        } else {
          this.errorMessage = 'Failed to create worker.';
        }
      }
    },
    clearForm() {
      const route = useRoute();
      this.newWorker = {
        firstName: '',
        lastName: '',
        username: '',
        password: '',
        gender: 'MALE',
        birthDate: '',
        role: 'WORKER',
        customerType: 'NO_TYPE',
        earnedPoints: 0,
        shoppingCart: null,
        blocked: false,
        factoryId: route.params.factoryId 
      };
    }
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

.form-label {
  font-weight: normal;
  margin-bottom: 5px;
}

/* Stilizacija za inpute i dugmad */
input[type="text"],
input[type="password"],
input[type="date"],
input[type="number"],
select,
button[type="submit"] {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

/* Fokus efekat na inputima */
input[type="text"]:focus,
input[type="password"]:focus,
input[type="date"]:focus,
input[type="number"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

/* Dugmad sa plavim outline stilom */
button[type="submit"] {
  border: 1px solid #007bff;
  background-color: transparent;
  color: #007bff;
  transition: background-color 0.3s ease, color 0.3s ease;
}

/* Hover efekat na dugmad */
button[type="submit"]:hover {
  background-color: #007bff;
  color: white;
}

button {
  font-size: 18px;
  margin-top: 20px;
}

/* Stilizacija za uspešne i greške poruke */
.text-danger {
  margin-top: 15px;
}

.text-success {
  margin-top: 15px;
}
</style>
