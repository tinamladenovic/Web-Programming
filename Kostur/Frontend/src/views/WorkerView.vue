<template>
  <div class="d-flex justify-content-center align-items-center vh-75">
    <div class="card p-5 shadow-lg" style="width: 100%; max-width: 1000px; background-color: #e9f5ff; margin-top: -50px;">
     
      <h2 class="text-center mb-4 blue-label">CHOCOLATES</h2>
      
      <!-- Lista čokolada -->
      <ul class="chocolates-list">
        <li v-for="chocolate in chocolates" :key="chocolate.id" @click="selectChocolate(chocolate)">
          <div class="chocolate-info">
            <span>{{ chocolate.name }}</span>
            <span><strong>Type:</strong> {{ chocolate.type }}</span>
            <span><strong>Kind:</strong> {{ chocolate.kind }}</span>
            <span><strong>Weight:</strong> {{ chocolate.weight }}g</span>
            <span><strong>Status:</strong> {{ chocolate.status }}</span>
            <span><strong>Quantity:</strong> {{ chocolate.quantity }} pcs</span>
          </div>
        </li>
      </ul>

      <!-- Sekcija za ažuriranje količine, samo za WORKER ulogu -->
      <div v-if="selectedChocolate && userRole === 'WORKER'" class="edit-quantity text-center">
        <h3>Update quantity for: {{ selectedChocolate.name }}</h3>
        <input v-model.number="newQuantity" type="number" min="0" placeholder="Enter new quantity" class="form-control half-width">
        <button @click="updateQuantity" class="btn btn-outline-primary mt-2">Update Quantity</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted } from 'vue';

export default {
  setup() {
    const chocolates = ref([]);
    const selectedChocolate = ref(null);
    const newQuantity = ref(0);
    const userRole = ref(null); // Dodavanje uloge korisnika

    const fetchWorkerData = async () => {
      try {
        // Dohvatanje trenutnog korisnika
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/currentUser', {
          withCredentials: true,
        });
        const worker = response.data;
        userRole.value = worker.role; // Provera uloge korisnika

        if (worker.role === 'WORKER') {
          // Fetch chocolates from the worker's factory
          const factoryResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolateFactoryObjects/${worker.factoryId}`, {
            withCredentials: true,
          });
          chocolates.value = factoryResponse.data.chocolates;
        } else {
          console.warn('Only workers can manage chocolate quantities.');
        }
      } catch (error) {
        console.error('Error fetching worker data:', error);
      }
    };

    const selectChocolate = (chocolate) => {
      selectedChocolate.value = chocolate;
      newQuantity.value = chocolate.quantity;
    };

    const updateQuantity = async () => {
      if (selectedChocolate.value && newQuantity.value >= 0) {
        try {
          const updatedChocolate = { ...selectedChocolate.value, quantity: newQuantity.value };
          console.log("Updating chocolate:", updatedChocolate);
          const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${selectedChocolate.value.id}/quantity`, updatedChocolate, {
            withCredentials: true,
          });
          selectedChocolate.value.quantity = newQuantity.value;
          alert('Quantity updated successfully.');
        } catch (error) {
          console.error('Error updating chocolate quantity:', error);
          if (error.response && error.response.status === 403) {
            alert('Access denied: You can only update quantities for chocolates in your factory.');
          } else {
            alert('An error occurred while updating quantity.');
          }
        }
      }
    };

    onMounted(() => {
      fetchWorkerData();
    });

    return {
      chocolates,
      selectedChocolate,
      newQuantity,
      selectChocolate,
      updateQuantity,
      userRole, // Vraćanje uloge korisnika
    };
  },
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
  background-color: #e9f5ff; /* Svetlo plava pozadina */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

h2 {
  color: #007bff;
  font-size: 2rem;
  font-weight: normal;
}

.chocolates-list {
  list-style-type: none;
  padding: 0;
}

.chocolates-list li {
  padding: 10px;
  cursor: pointer;
  background-color: #f2f2f2;
  margin-bottom: 10px;
  border-radius: 8px;
  text-align: center;
  transition: background-color 0.3s ease;
}

.chocolates-list li:hover {
  background-color: #e0e0e0;
}

.chocolate-info {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 10px;
  text-align: left;
}

/* Sekcija za ažuriranje količine */
.edit-quantity {
  margin-top: 20px;
}

.edit-quantity input {
  margin-right: 10px;
  padding: 10px;
  width: 50%;
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.edit-quantity input:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

.edit-quantity button {
  padding: 10px;
  background-color: transparent;
  color: #007bff;
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.edit-quantity button:hover {
  background-color: #007bff;
  color: white;
}
</style>
