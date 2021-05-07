export default function renderHTML(content:string) {
  return `<!DOCTYPE html>
  <html lang="en">
  
  <head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
      body{
        font-size:13px;
      }
    </style>
  </head>
  
  <body>
    ${content}
  </body>
  
  </html>`;
}
