<template>
  <nav>
    <router-link to="/">Chocolate Factories</router-link> &nbsp

    <!-- Link za Add Factory vidljiv samo adminima -->
    <router-link v-if="isAdmin" to="/addfactory">Add Factory</router-link> &nbsp

    <!-- Link za Users vidljiv samo adminima -->
    <router-link v-if="isAdmin" to="/users">Users</router-link> &nbsp

    <!-- Link za Manager Dashboard vidljiv samo menadžerima -->
    <router-link v-if="isManager" to="/managerview">Manager Dashboard</router-link> &nbsp

    <!-- Link za Worker Dashboard vidljiv samo radnicima -->
    <router-link v-if="isWorker" to="/workerview">Worker Dashboard</router-link> &nbsp

    <!-- Linkovi za Register i Login vidljivi samo ako korisnik nije ulogovan -->
    <router-link v-if="!isLoggedIn" to="/register">Register</router-link> &nbsp
    <router-link v-if="!isLoggedIn" to="/login">Login</router-link> &nbsp

    <!-- Linkovi za Logout i Profile vidljivi samo ako je korisnik ulogovan -->
    <router-link v-if="isLoggedIn" to="/logout">Logout</router-link> &nbsp
    <router-link v-if="isLoggedIn" to="/profile">Profile</router-link> &nbsp
  </nav>
  <router-view />
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      currentUser: null, // Čuvanje trenutnog korisnika
    };
  },
  computed: {
    isLoggedIn() {
      return !!this.currentUser; // Provera da li je korisnik ulogovan
    },
    isAdmin() {
      return this.currentUser && this.currentUser.role === 'ADMIN'; // Provera da li je korisnik admin
    },
    isManager() {
      return this.currentUser && this.currentUser.role === 'MANAGER'; // Provera da li je korisnik menadžer
    },
    isWorker() {
      return this.currentUser && this.currentUser.role === 'WORKER'; // Provera da li je korisnik radnik
    },
    isCustomer() {
      return this.currentUser && this.currentUser.role === 'CUSTOMER'; // Provera da li je korisnik kupac
    }
  },
  mounted() {
    this.fetchCurrentUser();
  },
  methods: {
    // Metoda za povlačenje trenutnog korisnika sa API-ja
    async fetchCurrentUser() {
      try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/currentUser', {
          withCredentials: true
        });
        if (response.data) {
          this.currentUser = response.data; // Postavljanje trenutnog korisnika
          console.log("Current user fetched:", this.currentUser); // Logovanje trenutnog korisnika
          localStorage.setItem('currentUser', JSON.stringify(this.currentUser)); // Čuvanje korisnika u localStorage
        } else {
          this.currentUser = null; // Resetuj korisnika ako nema podataka
          localStorage.removeItem('currentUser');
        }
      } catch (error) {
        console.error('Error fetching current user:', error.response ? error.response.data : error.message);
        this.currentUser = null; // Ako je došlo do greške, resetuj korisnika
        localStorage.removeItem('currentUser');
      }
    }
  }
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style>
