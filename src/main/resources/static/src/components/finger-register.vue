<template>
  <div id="hello">
    <div class="row margint" style="width:100%">
      <div class="col-md-2" style="border-right:1px solid #000;height:600px;overflow-y: scroll;">
        <h4>已注册的人员</h4>
        <ul>
          <li v-for="registered in regs">{{registered.code}}</li>
        </ul>
      </div>
      <div class="col-md-9">

        <h2>扫描到的指纹</h2>
        
        <div class="row">
           <div class="col-md-6">
              <div class="input-group">
                <input type="text" class="form-control" placeholder="服刑人员的编号" v-model="code">
                <span class="input-group-btn">
                  <button class="btn btn-default" type="button" :disabled="!isRegisterable" @click="register()">注册</button>
                </span>
              </div>
            </div>
            <div class="col-md-2">
              <input type="radio" v-model="identity" value="prison">注册为服刑人员
            </div>
            <div class="col-md-2">
              <input type="radio" v-model="identity" value="police">注册为管教人员
            </div>

        </div>
        <div class="row  margint">
            <div class="col-md-4" v-for="imgSrc in imgs"> 
              <img :src="imgSrc">
            </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import _ from "lodash"
export default {
  name: 'finger-collecting',
  data () {
    return {
      imgs:[],
      websocket: '',
      code:"",
      identity:'prison',
      regs:[]
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
      if(this.imgs.length < 3){
          this.imgs.push(msg.data);
      }
    }
  },
  methods:{
    register: function(){
      var identityInfo = {
          'code':this.code,
          'identity': this.identity,
          'op':'reg'
        }
      console.info("sending:" + identityInfo)
      console.info(this.websocket.readyState)
      if(this.websocket.readyState == this.websocket.OPEN){
          this.websocket.send(JSON.stringify(identityInfo))
          this.imgs = [];
          this.regs.push(identityInfo)
      }
      this.identity = 'prison'
    }
  },
  computed:{
    isRegisterable: function(){
      if(this.imgs.length == 3 && this.code.length > 0){
        return true
      }else {
        return false
      }
    }
  }
}
</script>

<style scoped>
.margint {
  margin-top: 10px
}
</style>
