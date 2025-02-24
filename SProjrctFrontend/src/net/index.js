import axios from "axios";

const defaultError=()=>ElMessage.error('some errors occur')
const defaultFailure=(message)=>  ElMessage({
    message: message,
    grouping: true,
    type: 'warning',
})
 function post(url,data,success,failure=defaultFailure,error=defaultError) {
     axios.post(url,data,{
         headers:{
             'Content-Type': 'application/x-www-form-urlencoded'
         },
         withCredentials:true
     }).then(({data})=>{
         if (data.success){
             success(data.message,data.status)
         }else {
             failure(data.message,data.status)
         }
     }).catch(error)
 }

function get(url,success,failure = defaultFailure, error = defaultError) {
    axios.get(url,{
        withCredentials:true
    }).then(({data})=>{
        if (data.success){
            success(data.message,data.status)
        }else {
            failure(data.message,data.status)
        }
    }).catch(error)
}



export { get, post }