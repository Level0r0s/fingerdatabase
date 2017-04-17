<template>
  <div id="hello">
    <img src="http://vuejs.org/images/logo.png">
    <h1>{{ msg }}</h1>
    <h2>Essential Links</h2>

    <h2>Ecosystem</h2>
    <ul>
      <li v-for="imgSrc in imgs"> <img :src="imgSrc"></li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'hello',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App',
      imgs:[],
      websocket: ''
    }
  },
  mounted:function(){
    this.websocket = new WebSocket("ws://localhost:8090/finger");
    this.websocket.onopen = _ =>{
      console.info("connected successfully!")
    }

    this.websocket.onmessage = (msg) => {
      console.info("recieved!");
      console.info(msg.data);
      this.imgs.push(msg.data);
    }
  }
}
</script>

<style scoped>
#hello {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
