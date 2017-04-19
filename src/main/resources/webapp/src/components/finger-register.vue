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
        </div>
        <div class="row">
           <div class="col-md-2">
              <input type="radio" v-model="identity" value="prison">注册为服刑人员
            </div>
            <div class="col-md-2">
              <input type="radio" v-model="identity" value="police">注册为管教人员
            </div>
        </div>
        <canvas id="canvas"/>
  
        <div class="row" style="top: -60px; position: relative">
          <div class="col-md-offset-6 col-md-3">
            <video id="video"></video>
            <button id="startbutton" @click="takepicture()" :disabled="code.length == 0">Take photo</button> 
          </div>
          <div class="col-md-3">
              <img id="photo" alt="" :src="headPic">
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
var width = 250;    // We will scale the photo width to this
var height = 0;     // This will be computed based on the input stream

// |streaming| indicates whether or not we're currently streaming
// video from the camera. Obviously, we start at false.

var streaming = false;

// The various HTML elements we need to configure or control. These
// will be set by the startup() function.

var video = null;
var canvas = null;
var photo = null;
var startbutton = null;
export default {
  name: 'finger-collecting',
  data () {
    return {
      imgs:[],
      websocket: '',
      code:"",
      identity:'prison',
      regs:[],
      headPic:""
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

    video = document.getElementById('video');
    canvas = document.getElementById('canvas');
    photo = document.getElementById('photo');
    startbutton = document.getElementById('startbutton');

    navigator.getMedia = ( navigator.getUserMedia ||
                           navigator.webkitGetUserMedia ||
                           navigator.mozGetUserMedia ||
                           navigator.msGetUserMedia);

    navigator.getMedia(
      {
        video: true,
        audio: false
      },
      function(stream) {
        if (navigator.mozGetUserMedia) {
          video.mozSrcObject = stream;
        } else {
          var vendorURL = window.URL || window.webkitURL;
          video.src = vendorURL.createObjectURL(stream);
        }
        video.play();
      },
      function(err) {
        console.log("An error occured! " + err);
      }
    );

    video.addEventListener('canplay', function(ev){
      if (!streaming) {
        height = video.videoHeight / (video.videoWidth/width);
      
        // Firefox currently has a bug where the height can't be read from
        // the video, so we will make assumptions if this happens.
      
        if (isNaN(height)) {
          height = width / (4/3);
        }
      
        video.setAttribute('width', width);
        video.setAttribute('height', height);
        canvas.setAttribute('width', width);
        canvas.setAttribute('height', height);
        streaming = true;
      }
    }, false);

    
    this.clearphoto();
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
      this.clearphoto();
    },
    clearphoto: function(){
      this.headPic = ""
    },
    takepicture: function(){
      var context = canvas.getContext('2d');
      if (width && height) {
        canvas.width = width;
        canvas.height = height;
        context.drawImage(video, 0, 0, width, height);
        var data = canvas.toDataURL('image/png');
        this.headPic = data;
        var download = document.createElement("a");
        download.href = data;
        download.download = this.code;
        download.click()
      } else {
        this.clearphoto();
      }
    }

  },
  computed:{
    isRegisterable: function(){
      if(this.identity == 'prison' && this.headPic.length == 0){
        return false;
      }
      if(this.imgs.length == 3 && this.code.length > 0 ){
        return true
      }
      return false
    }
  }
}
</script>

<style scoped>
.margint {
  margin-top: 10px
}

#video {
  border: 1px solid black;
  box-shadow: 2px 2px 3px black;
  width:250px;
  height:200px;
}

#photo {
  border: 1px solid black;
  box-shadow: 2px 2px 3px black;
  width:250px;
  height:200px;
}

#canvas {
  display:none;
}

.camera {
  width: 250px;
  display:inline-block;
}

.output {
  width: 250px;
  display:inline-block;
}

#startbutton {
  display:block;
  position:relative;
  margin-left:auto;
  margin-right:auto;
  bottom:32px;
  background-color: rgba(0, 150, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0px 0px 1px 2px rgba(0, 0, 0, 0.2);
  font-size: 14px;
  font-family: "Lucida Grande", "Arial", sans-serif;
  color: rgba(255, 255, 255, 1.0);
}

.contentarea {
  font-size: 16px;
  font-family: "Lucida Grande", "Arial", sans-serif;
  width: 760px;
}
</style>
