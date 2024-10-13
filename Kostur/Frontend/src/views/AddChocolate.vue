<template>
  <div class="d-flex justify-content-center align-items-center vh-75"> 
    <div class="card p-5 shadow-lg text-center" style="width: 100%; max-width: 500px; margin-top: -50px;">
      
      <form @submit.prevent="addChocolate" class="chocolate-form">
        <div class="row mb-3">
          <label for="name" class="col-sm-3 col-form-label text-start">Name:</label>
          <div class="col-sm-9">
            <input v-model="chocolate.name" id="name" class="form-control form-control-lg" required />
          </div>
        </div>
        
        <div class="row mb-3">
          <label for="price" class="col-sm-3 col-form-label text-start">Price:</label>
          <div class="col-sm-9">
            <input v-model.number="chocolate.price" id="price" type="number" class="form-control form-control-lg" required />
          </div>
        </div>
        
        <div class="row mb-3">
          <label for="type" class="col-sm-3 col-form-label text-start">Type:</label>
          <div class="col-sm-9">
            <select v-model="chocolate.type" id="type" class="form-select form-control-lg" required>
              <option v-for="type in chocolateTypes" :value="type">{{ type }}</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="kind" class="col-sm-3 col-form-label text-start">Kind:</label>
          <div class="col-sm-9">
            <select v-model="chocolate.kind" id="kind" class="form-select form-control-lg" required>
              <option v-for="kind in chocolateKinds" :value="kind">{{ kind }}</option>
            </select>
          </div>
        </div>

        <div class="row mb-3">
          <label for="weight" class="col-sm-3 col-form-label text-start">Weight (grams):</label>
          <div class="col-sm-9">
            <input v-model.number="chocolate.weight" id="weight" type="number" class="form-control form-control-lg" required />
          </div>
        </div>

        <div class="row mb-3">
          <label for="description" class="col-sm-3 col-form-label text-start">Description:</label>
          <div class="col-sm-9">
            <textarea v-model="chocolate.description" id="description" class="form-control form-control-lg" required></textarea>
          </div>
        </div>

        <div class="row mb-3">
          <label for="picture" class="col-sm-3 col-form-label text-start">Picture:</label>
          <div class="col-sm-9">
            <input v-model="chocolate.picture" id="picture" class="form-control form-control-lg" required />
          </div>
        </div>

        <div class="row">
          <div class="col-12">
            <button type="submit" class="btn btn-primary w-100 btn-lg">ADD CHOCOLATE</button>
          </div>
        </div>
      </form>
      <p v-if="errorMessage" class="text-danger text-center mt-3">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      chocolate: {
        name: '',
        price: 0,
        type: '',
        kind: '',
        weight: 0,
        description: '',
        factoryId: '',
        picture: ''
      },
      chocolateTypes: ['PLAIN', 'FOR_COOKING', 'FOR_DRINKING'],
      chocolateKinds: ['DARK', 'MILK', 'WHITE'],
      errorMessage: '',
    };
  },
  created() {
    this.getFactoryId();
  },
  methods: {
    async getFactoryId() {
      try {
        const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/users/currentManagerFactory', {
          withCredentials: true
        });
        this.chocolate.factoryId = response.data.factoryId; // Postavi factoryId
      } catch (error) {
        console.error('Error fetching factory ID:', error);
        this.errorMessage = 'Failed to fetch factory ID.';
      }
    },
    async addChocolate() {
      try {
        this.chocolate.quantity = 0;
        this.chocolate.status = 'OUT_OF_STOCK';

        await axios.post('http://localhost:8080/WebShopAppREST/rest/chocolates', this.chocolate, {
          withCredentials: true
        });
        this.$router.push({ name: 'managerview' });
      } catch (error) {
        console.error('Error adding chocolate:', error);
        this.errorMessage = 'Failed to add chocolate.';
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
  text-align: center;
}

.form-label {
  font-weight: normal;
}

input[type="text"],
input[type="number"],
select,
textarea,
button {
  border: 1px solid #007bff;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input[type="text"]:focus,
input[type="number"]:focus,
textarea:focus,
select:focus {
  border-color: #007bff;
  box-shadow: 0 0 5px rgba(0, 123, 255, 0.25);
}

button[type="submit"],
.btn-outline-secondary {
  border: 1px solid #007bff;
  background-color: transparent;
  color: #007bff;
  font-size: 1.1rem;
  transition: background-color 0.3s ease, color 0.3s ease;
}

button[type="submit"]:hover,
.btn-outline-secondary:hover {
  background-color: #007bff;
  color: white;
}

button[type="submit"] {
  margin-top: 20px;
}

textarea {
  height: 120px;
}

.text-danger {
  margin-top: 15px;
}
</style>
