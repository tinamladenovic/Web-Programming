<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 500px; margin-top: -50px;">
     
      <p class="text-center">Are you sure you want to logout?</p>
      <div class="row">
        <div class="col-12">
          <button @click="handleLogout" class="btn btn-primary w-100">Logout</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  methods: {
    async handleLogout() {
      if (confirm('Are you sure you want to logout?')) {
        try {
          const response = await fetch('http://localhost:8080/WebShopAppREST/rest/logout', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json'
            }
          });
  
          if (response.ok) {
            console.log('Logout successful');
            localStorage.removeItem('authToken'); // Primer
            this.$router.push('/login');
          } else {
            console.error('Error logging out:', response.statusText);
            alert('Logout failed. Please try again.');
          }
        } catch (error) {
          console.error('Error logging out:', error);
          alert('An error occurred during logout. Please try again.');
        } finally {
          this.$router.push('/login');
        }
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
  margin-top: -50px; /* Podizanje sekcije */
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
