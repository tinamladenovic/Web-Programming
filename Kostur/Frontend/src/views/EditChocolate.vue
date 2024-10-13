<template>
  <div class="edit-chocolate">
    <h2>Edit Chocolate</h2>
    <form @submit.prevent="updateChocolate">
      <div class="form-group">
        <label for="name">Name:</label>
        <input type="text" id="name" v-model="chocolate.name" required>
      </div>
      <div class="form-group">
        <label for="price">Price:</label>
        <input type="number" id="price" v-model="chocolate.price" required>
      </div>
      <div class="form-group">
        <label for="type">Type:</label>
        <select id="type" v-model="chocolate.type" required>
          <option value="PLAIN">PLAIN</option>
          <option value="FOR_COOKING">FOR_COOKING</option>
          <option value="FOR_DRINKING">FOR_DRINKING</option>
        </select>
      </div>
      <div class="form-group">
        <label for="kind">Kind:</label>
        <select id="kind" v-model="chocolate.kind" required>
          <option value="DARK">DARK</option>
          <option value="MILK">MILK</option>
          <option value="WHITE">WHITE</option>
        </select>
      </div>
      <div class="form-group">
        <label for="weight">Weight:</label>
        <input type="number" id="weight" v-model="chocolate.weight" required>
      </div>
      <div class="form-group">
        <label for="description">Description:</label>
        <textarea id="description" v-model="chocolate.description" required></textarea>
      </div>
      <div class="form-group">
        <label for="picture">Picture:</label>
        <input type="text" id="picture" v-model="chocolate.picture" required>
      </div>
      <button type="submit">Update Chocolate</button>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      chocolate: {
        id: '',
        name: '',
        price: 0,
        type: 'PLAIN',
        kind: 'DARK',
        weight: 0,
        description: '',
        picture: '',
        status: 'IN_STOCK',
        quantity: 0
      },
      errorMessage: ''
    };
  },
  async created() {
    try {
      // Učitavanje čokolade po ID-u bez provere uloge korisnika
      const id = this.$route.params.id;
      const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${id}`);
      this.chocolate = response.data;
      
    } catch (error) {
      this.errorMessage = 'Failed to load chocolate details.';
      console.error('Error loading chocolate:', error);
    }
  },
  methods: {
    async updateChocolate() {
      try {
        const response = await axios.put(`http://localhost:8080/WebShopAppREST/rest/chocolates/${this.chocolate.id}`, this.chocolate);
        console.log('Chocolate updated:', response.data);
        // Nakon uspešnog ažuriranja, preusmeri na ManagerView
        this.$router.push('/managerview');
      } catch (error) {
        this.errorMessage = 'Failed to update chocolate.';
        console.error('Error updating chocolate:', error);
      }
    }
  }
};
</script>

<style scoped>
.edit-chocolate {
  max-width: 600px;
  margin: 0 auto;
  padding: 30px;
  border: 1px solid #007bff;
  border-radius: 8px;
  background-color: #f0f8ff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #007bff;
  text-align: center;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #007bff;
  font-weight: bold;
}

input, select, textarea {
  width: 100%;
  padding: 10px;
  font-size: 1.1rem;
  border: 1px solid #007bff;
  border-radius: 8px;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input:focus, select:focus, textarea:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

button {
  padding: 10px 20px;
  background-color: transparent;
  color: #007bff;
  border: 1px solid #007bff;
  cursor: pointer;
  font-size: 16px;
  border-radius: 8px;
  transition: background-color 0.3s ease, color 0.3s ease;
  width: 100%;
}

button:hover {
  background-color: #007bff;
  color: white;
}

.error {
  color: red;
  text-align: center;
  margin-top: 10px;
}
</style>
