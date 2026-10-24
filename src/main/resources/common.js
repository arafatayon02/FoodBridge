const apiBase = localStorage.getItem('foodbridge_api') || 'http://localhost:8080';
function value(id){return document.getElementById(id).value.trim()}
function token(){return sessionStorage.getItem('foodbridge_token') || ''}
async function call(path, method='GET', body=null){
    const headers={'Accept':'application/json'}; if(body!==null)headers['Content-Type']='application/json';
    if(token())headers.Authorization='Bearer '+token();
    const response=await fetch(apiBase+path,{method,headers,body:body===null?undefined:JSON.stringify(body)});
    const raw=await response.text();let data;try{data=JSON.parse(raw)}catch{data={message:raw}}
    if(!response.ok)throw new Error(data.message || data.detail || `HTTP ${response.status}`);return data;
}
async function execute(out,fn){const node=document.getElementById(out);node.textContent='Loading...';try{const data=await fn();node.textContent=JSON.stringify(data,null,2);return data}catch(e){node.textContent='Error: '+e.message}}
function renderList(out,items,fields){const el=document.getElementById(out);el.replaceChildren();for(const item of items){let row=document.createElement('article');for(const field of fields){const p=document.createElement('p');p.textContent=field+': '+(item[field]??'');row.append(p)}el.append(row)}if(!items.length)el.textContent='No records found.'}
