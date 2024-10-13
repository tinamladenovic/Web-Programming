<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 500px; margin-top: -50px;">
      <form @submit.prevent="createUser" class="manager-form">
        <div class="row mb-3">
          <label for="firstName" class="col-sm-3 col-form-label text-start">First Name:</label>
          <div class="col-sm-9">
            <input type="text" id="firstName" v-model="newUser.firstName" name="firstName" autocomplete="given-name" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="lastName" class="col-sm-3 col-form-label text-start">Last Name:</label>
          <div class="col-sm-9">
            <input type="text" id="lastName" v-model="newUser.lastName" name="lastName" autocomplete="family-name" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="username" class="col-sm-3 col-form-label text-start">Username:</label>
          <div class="col-sm-9">
            <input type="text" id="username" v-model="newUser.username" name="username" autocomplete="username" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="password" class="col-sm-3 col-form-label text-start">Password:</label>
          <div class="col-sm-9">
            <input type="password" id="password" v-model="newUser.password" name="password" autocomplete="new-password" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="gender" class="col-sm-3 col-form-label text-start">Gender:</label>
          <div class="col-sm-9">
            <select id="gender" v-model="newUser.gender" name="gender" autocomplete="sex" class="form-control" required>
              <option value="MALE">MALE</option>
              <option value="FEMALE">FEMALE</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="birthDate" class="col-sm-3 col-form-label text-start">Birth Date:</label>
          <div class="col-sm-9">
            <input type="date" id="birthDate" v-model="newUser.birthDate" name="birthDate" autocomplete="bday" class="form-control" required>
          </div>
        </div>

        <div class="row mb-3">
          <label for="role" class="col-sm-3 col-form-label text-start">Role:</label>
          <div class="col-sm-9">
            <select id="role" v-model="newUser.role" name="role" class="form-control" disabled>
              <option value="MANAGER">MANAGER</option>
            </select>
          </div>
        </div>

        <div class="row">
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">ADD MANAGER</button>
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

export default {
  data() {
    return {
      newUser: {
        firstName: '',
        lastName: '',
        username: '',
        password: '',
        gender: 'MALE',
        birthDate: '',
        role: 'MANAGER',
        customerType: {
          name: 'NO_TYPE',
          discount: 0.0,
          requiredPoints: 0
        },
        earnedPoints: 0
      },
      errorMessage: '',
      successMessage: ''
    };
  },
  methods: {
    async createUser() {
      try {
        const response = await axios.post('http://localhost:8080/WebShopAppREST/rest/users/createmanager', this.newUser, {
          withCredentials: true  // Za slanje kolačića sesije
        });
        this.successMessage = 'Manager created successfully!';
        this.errorMessage = '';
        this.clearForm();

        // Preusmeravanje na AddFactory stranicu nakon uspešnog kreiranja menadžera
        this.$router.push('/addfactory');
      } catch (error) {
        this.successMessage = '';
        if (error.response && error.response.data && error.response.data.message) {
          this.errorMessage = error.response.data.message;
        } else {
          this.errorMessage = 'Failed to create manager.';
        }
      }
    },
    clearForm() {
      this.newUser = {
        firstName: '',
        lastName: '',
        username: '',
        password: '',
        gender: 'MALE',
        birthDate: '',
        role: 'MANAGER',
        customerType: {
          name: 'NO_TYPE',
          discount: 0.0,
          requiredPoints: 0
        },
        earnedPoints: 0
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

input[type="text"],
input[type="password"],
input[type="date"],
select,
button[type="submit"] {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
input[type="password"]:focus,
input[type="date"]:focus,
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
