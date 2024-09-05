<template>
  <nav v-if="userRole">
    <router-link to="/">Chocolate Factories</router-link> |

    <!-- Link za Add Factory vidljiv samo ako je userRole ADMIN -->
    <router-link v-if="userRole === 'ADMIN'" to="/addfactory">Add Factory</router-link> |

   

    <!-- Link za Users vidljiv samo ako je userRole ADMIN -->
    <router-link v-if="userRole === 'ADMIN'" to="/users">Users</router-link> |

    <!-- Link za Manager Dashboard vidljiv samo ako je userRole MANAGER -->
    <router-link v-if="userRole === 'MANAGER'" to="/managerview">Manager Dashboard</router-link> |

    <!-- Link za Worker Dashboard vidljiv samo ako je userRole WORKER -->
    <router-link v-if="userRole === 'WORKER'" to="/workerview">Worker Dashboard</router-link> |

    <router-link to="/register">Register</router-link> |
    <router-link to="/login">Login</router-link> |
    <router-link to="/logout">Logout</router-link> |
    <router-link to="/profile">Profile</router-link> |
  </nav>
  <router-view/>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      userRole: null, // Default vrednost za userRole
    };
  },
  mounted() {
    this.getCurrentUserRole();
  },
  methods: {
    async getCurrentUserRole() {
      try {
        // Poziv API-ja da se dobije trenutni korisnik
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/currentUser', {
          withCredentials: true,
        });
        const currentUser = response.data;
        this.userRole = currentUser.role; // Postavljanje uloge korisnika
        console.log('Trenutna uloga:', this.userRole); // Provera uloge u konzoli
      } catch (error) {
        console.error('Greška pri dohvatanju trenutnog korisnika:', error);
      }
    },
  },
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
