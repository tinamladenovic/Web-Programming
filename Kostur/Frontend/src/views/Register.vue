<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 500px; margin-top: -50px;">
    
      <form @submit.prevent="handleRegister">
        <div class="row mb-3">
          <label for="username" class="col-sm-3 col-form-label text-start">Username:</label>
          <div class="col-sm-9">
            <input type="text" id="username" v-model="username" class="form-control" required autocomplete="username">
          </div>
        </div>
        <div class="row mb-3">
          <label for="password" class="col-sm-3 col-form-label text-start">Password:</label>
          <div class="col-sm-9">
            <input type="password" id="password" v-model="password" class="form-control" required autocomplete="new-password">
          </div>
        </div>
        <div class="row mb-3">
          <label for="confirmPassword" class="col-sm-3 col-form-label text-start">Confirm Password:</label>
          <div class="col-sm-9">
            <input type="password" id="confirmPassword" v-model="confirmPassword" class="form-control" required autocomplete="new-password">
          </div>
        </div>
        <div class="row mb-3">
          <label for="firstName" class="col-sm-3 col-form-label text-start">First Name:</label>
          <div class="col-sm-9">
            <input type="text" id="firstName" v-model="firstName" class="form-control" required autocomplete="given-name">
          </div>
        </div>
        <div class="row mb-3">
          <label for="lastName" class="col-sm-3 col-form-label text-start">Last Name:</label>
          <div class="col-sm-9">
            <input type="text" id="lastName" v-model="lastName" class="form-control" required autocomplete="family-name">
          </div>
        </div>
        <div class="row mb-3">
          <label for="gender" class="col-sm-3 col-form-label text-start">Gender:</label>
          <div class="col-sm-9">
            <select id="gender" v-model="gender" class="form-control" required autocomplete="sex">
              <option value="">Select Gender</option>
              <option value="MALE">MALE</option>
              <option value="FEMALE">FEMALE</option>
            </select>
          </div>
        </div>
        <div class="row mb-3">
          <label for="birthDate" class="col-sm-3 col-form-label text-start">Date of Birth:</label>
          <div class="col-sm-9">
            <input type="date" id="birthDate" v-model="birthDate" class="form-control" required autocomplete="bday">
          </div>
        </div>
        <div class="row">
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Register</button>
          </div>
        </div>
      </form>
      <p v-if="errorMessage" class="text-danger text-center mt-3">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      gender: '',
      birthDate: '',
      errorMessage: ''
    };
  },
  methods: {
    async handleRegister() {
      if (this.password !== this.confirmPassword) {
        this.errorMessage = 'Passwords do not match';
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/WebShopAppREST/rest/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: this.username,
            password: this.password,
            firstName: this.firstName,
            lastName: this.lastName,
            gender: this.gender,
            birthDate: this.birthDate,
            role: 'CUSTOMER'
          })
        });

        if (response.ok) {
          // Handle successful registration
          console.log('Registration successful');
          // Reset form fields
          this.username = '';
          this.password = '';
          this.confirmPassword = '';
          this.firstName = '';
          this.lastName = '';
          this.gender = '';
          this.birthDate = '';
          this.errorMessage = '';
          // Redirect to login page or set user state
          this.$router.push('/login');
        } else {
          // Handle errors
          this.errorMessage = await response.text() || 'Registration failed';
        }
      } catch (error) {
        console.error('Error registering:', error);
        this.errorMessage = 'An error occurred. Please try again.';
      }
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
  color: #007bff;
  text-align: center;
  font-size: 2rem;
  font-weight: normal;
}

.form-label {
  font-weight: normal;
}

input[type="text"],
input[type="password"],
input[type="date"],
select,
button {
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

.text-danger {
  margin-top: 15px;
}
</style>
