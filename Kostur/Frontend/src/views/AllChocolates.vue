<template>
  <div class="all-chocolates">
    <table>
      <thead>
        <tr>
          <th>Slika</th>
          <th>Naziv</th>
          <th>Cena</th>
          <th>Tip</th>
          <th>Vrsta</th>
          <th>Težina</th>
          <th>Opis</th>
          <th>Fabrika</th>
          <th v-if="isManagerOrWorker">Izmeni količinu</th> <!-- Prikazuje dugme za izmenu količine samo ako je korisnik menadžer ili radnik -->
          <th v-if="isManager">EDIT</th> <!-- Prikazuje dugme za editovanje samo ako je korisnik menadžer -->
          <th v-if="isManager">DELETE</th> <!-- Prikazuje dugme za brisanje samo ako je korisnik menadžer -->
        </tr>
      </thead>
      <tbody>
        <tr v-for="chocolate in filteredChocolates" :key="chocolate.id">
          <td><img :src="getChocolatePicture(chocolate.picture)" :alt="chocolate.name" class="chocolate-picture" /></td>
          <td>{{ chocolate.name }}</td>
          <td>{{ chocolate.price }}</td>
          <td>{{ chocolate.type }}</td>
          <td>{{ chocolate.kind }}</td>
          <td>{{ chocolate.weight }}</td>
          <td>{{ chocolate.description }}</td>
          <td>{{ getFactoryName(chocolate.factoryId) }}</td>
          <td v-if="isWorker"><button @click="editQuantity(chocolate)">Izmeni količinu</button></td> <!-- Dugme za izmenu količine koje je vidljivo samo radnicima -->
          <td v-if="isManager"><button @click="editChocolate(chocolate.id)">EDIT</button></td> <!-- Dugme za editovanje vidljivo samo menadžerima -->
          <td v-if="isManager"><button @click="deleteChocolate(chocolate.id)">DELETE</button></td> <!-- Dugme za brisanje vidljivo samo menadžerima -->
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      chocolates: [], // Svi čokoladni proizvodi
      factories: [], // Sve fabrike
      currentUser: null // Trenutno ulogovan korisnik
    };
  },
  async created() {
    await this.fetchCurrentUser(); // Učitavanje trenutnog korisnika
    await this.fetchFactories(); // Učitavanje svih fabrika
    await this.fetchChocolates(); // Učitavanje svih čokolada
  },
  computed: {
    filteredChocolates() {
      if (this.isWorker) {
        // Ako je korisnik radnik, prikazujemo samo čokolade iz njegove fabrike
        return this.chocolates.filter(chocolate => chocolate.factoryId === this.currentUser.factoryId);
      }
      // Ako nije radnik, prikazujemo sve čokolade
      return this.chocolates;
    },
    isManager() {
      // Proveravamo da li je korisnik menadžer
      return this.currentUser && this.currentUser.role === 'MANAGER';
    },
    isWorker() {
      // Proveravamo da li je korisnik radnik
      return this.currentUser && this.currentUser.role === 'WORKER';
    },
    isManagerOrWorker() {
      // Proveravamo da li je korisnik menadžer ili radnik
      return this.isManager || this.isWorker;
    }
  },
  methods: {
    async fetchChocolates() {
      try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/chocolates');
        console.log('Loaded chocolates:', response.data);
        this.chocolates = response.data; // Skladištimo sve čokolade u promenljivu
      } catch (error) {
        console.error('Greška pri preuzimanju čokolada:', error);
      }
    },
    async fetchFactories() {
      try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/chocolateFactoryObjects');
        console.log('Loaded factories:', response.data);
        this.factories = response.data; // Skladištimo sve fabrike u promenljivu
      } catch (error) {
        console.error('Greška pri preuzimanju fabrika:', error);
      }
    },
    async fetchCurrentUser() {
      try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/currentUser');
        console.log('Current user:', response.data);
        this.currentUser = response.data; // Postavljamo trenutnog korisnika
      } catch (error) {
        console.error('Greška pri preuzimanju trenutnog korisnika:', error);
      }
    },
    getFactoryName(factoryId) {
      const factory = this.factories.find(f => f.factoryId === factoryId); // Pronalaženje fabrike po ID-u
      console.log('Factory ID:', factoryId, 'Factory:', factory);
      return factory ? factory.name : 'Nepoznato'; // Vraćamo naziv fabrike ili 'Nepoznato' ako nije pronađena
    },
    getChocolatePicture(pictureName) {
      return `/images/${pictureName}`; // Generišemo putanju do slike čokolade
    },
    editChocolate(id) {
      this.$router.push({ name: 'editchocolate', params: { id } }); // Navigacija ka ruti za uređivanje čokolade
    },
    async deleteChocolate(id) {
      try {
        await axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`); // Brisanje čokolade putem API-ja
        this.chocolates = this.chocolates.filter(chocolate => chocolate.id !== id); // Ažuriramo listu čokolada
      } catch (error) {
        console.error('Greška pri brisanju čokolade:', error);
      }
    },
    editQuantity(chocolate) {
      const newQuantity = prompt(`Unesite novu količinu za ${chocolate.name}:`, chocolate.quantity); // Unos nove količine putem prompta
      if (newQuantity !== null) {
        this.updateQuantity(chocolate.id, parseInt(newQuantity, 10)); // Ažuriramo količinu ako je unesena nova vrednost
      }
    },
    async updateQuantity(id, newQuantity) {
      try {
        await axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}/quantity`, { quantity: newQuantity }); // Slanje nove količine na server
        const chocolate = this.chocolates.find(chocolate => chocolate.id === id);
        if (chocolate) {
          chocolate.quantity = newQuantity; // Ažuriranje količine u lokalnoj listi
        }
      } catch (error) {
        console.error('Greška pri ažuriranju količine:', error);
      }
    }
  }
};
</script>

<style scoped>
.all-chocolates {
  overflow-x: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f5e7f5; /* Svetlija lila boja za zaglavlje */
  font-weight: bold;
  cursor: pointer;
}

.chocolate-picture {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

button {
  padding: 5px 10px;
  margin: 2px;
  background-color: transparent;
  color: #007bff;
  border: 1px solid #007bff;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}

button:hover {
  background-color: #007bff;
  color: white;
}

input[type="text"],
input[type="number"],
select,
button {
  border-radius: 8px;
  font-size: 1.1rem;
}

input[type="text"]:focus,
input[type="number"]:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

</style>
