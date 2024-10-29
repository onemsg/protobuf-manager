const devhost = "http://localhost:5000"
const cookie = "auth_name=SuperTestUser;auth_token=123456"


async function main() {
  const res = await fetch(devhost + "/api/protobuf/tools/generate-client-jar", {
    method: "POST",
    headers: {
      "content-type": "application/json",
    },
    body: JSON.stringify({
      protobufId: 1
    }),
  })

  const body = await res.text()
  console.log(res.status, res.statusText)
  console.log(res.headers)
  console.log(body)
}

main();