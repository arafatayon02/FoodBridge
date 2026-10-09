document.getElementById('login').onclick=()=>execute('loginOut',async()=>{const r=await call('/api/admin/auth/login','POST',{email:value('email'),password:value('password')});sessionStorage.setItem('foodbridge_token',r.token||r.accessToken||'');return {message:'Admin signed in',role:r.role}});
document.getElementById('summary').onclick=()=>execute('queue',()=>call('/api/admin/dashboard/summary'));
for(const type of ['shops','donors'])document.getElementById(type).onclick=()=>execute('queue',async()=>{let data=await call('/api/admin/'+(type==='shops'?'supershops':'donors')+'/pending');renderList('records',data,['id','businessName','status']);return {count:data.length}});
for(const [button,approve] of [['approve',true],['reject',false]])document.getElementById(button).onclick=()=>execute('decision',()=>call('/api/admin/'+value('kind')+'/'+encodeURIComponent(value('recordId'))+'/verify','PUT',{approve,rejectionReason:value('reason')}));

document.getElementById('register').onclick=()=>execute('rout',()=>call('/api/supershops/register','POST',{businessName:value('business'),address:value('address'),contactPerson:value('contact'),contactPhone:value('phone'),ownerName:value('owner'),ownerEmail:value('email'),ownerPassword:value('password')}));
document.getElementById('login').onclick=()=>execute('lout',async()=>{const r=await call('/api/auth/login','POST',{email:value('loginEmail'),password:value('loginPass')});sessionStorage.setItem('foodbridge_token',r.token||r.accessToken||'');return {message:'Signed in',role:r.role,supershopId:r.supershopId}});
document.getElementById('forgot').onclick=()=>execute('pout',()=>call('/api/branch-password/forgot','POST',{email:value('resetEmail')}));
document.getElementById('reset').onclick=()=>execute('pout',()=>call('/api/branch-password/reset','POST',{email:value('resetEmail'),code:value('otp'),newPassword:value('newPass')}));
const id=()=>encodeURIComponent(value('branch'));
document.getElementById('access').onclick=()=>execute('sout',()=>call('/api/branches/'+id()+'/me'));
document.getElementById('staffList').onclick=()=>execute('sout',()=>call('/api/branches/'+id()+'/staff'));
document.getElementById('addStaff').onclick=()=>execute('sout',()=>call('/api/supershops/'+id()+'/staff','POST',{name:value('staffName'),email:value('staffEmail'),password:value('staffPass')}));
