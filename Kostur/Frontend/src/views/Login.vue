<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 500px; margin-top: -50px;">
      
      <form @submit.prevent="handleLogin">
        <div class="row mb-3">
          <label for="username" class="col-sm-3 col-form-label text-start">Username</label>
          <div class="col-sm-9">
            <input type="text" id="username" v-model="username" class="form-control" required autocomplete="username">
          </div>
        </div>
        <div class="row mb-3">
          <label for="password" class="col-sm-3 col-form-label text-start">Password</label>
          <div class="col-sm-9">
            <input type="password" id="password" v-model="password" class="form-control" required autocomplete="current-password">
          </div>
        </div>
        <div class="row">
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100">Login</button>
          </div>
        </div>
      </form>
      <p v-if="errorMessage" class="text-danger text-center mt-3">{{ errorMessage }}</p>
      <p class="text-center mt-3">
        New user? <router-link to="/register">Register here</router-link>
      </p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      errorMessage: ''
    };
  },
  methods: {
    async handleLogin() {
      try {
        const response = await fetch('http://localhost:8080/WebShopAppREST/rest/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            username: this.username,
            password: this.password
          }),
          credentials: 'include'
        });

        if (!response.ok) {
          throw new Error('Invalid username or password');
        }

        const userData = await response.json();
        localStorage.setItem('user', JSON.stringify(userData));

        const currentUserResponse = await fetch('http://localhost:8080/WebShopAppREST/rest/currentUser', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json'
          },
          credentials: 'include'
        });

        if (!currentUserResponse.ok) {
          throw new Error('Failed to fetch current user');
        }

        const currentUserData = await currentUserResponse.json();
        this.$router.push('/');
      } catch (error) {
        this.errorMessage = error.message || 'An error occurred. Please try again.';
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
button {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
input[type="password"]:focus {
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
