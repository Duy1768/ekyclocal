const secretKeyInput = document.getElementById("secretKey");
const outputBox = document.getElementById("consoleOutput");
const faceComparePreview = document.getElementById("faceComparePreview");
const idCardPreview = document.getElementById("idCardPreview");
const selfiePreview = document.getElementById("selfiePreview");

const customerForm = document.getElementById("customerForm");
const faceForm = document.getElementById("faceForm");
const livenessForm = document.getElementById("livenessForm");
const clearOutputButton = document.getElementById("clearOutput");

const API_BASE = "http://localhost:8080";

function prettyLog(value) {
  outputBox.textContent =
      typeof value === "string"
          ? value
          : JSON.stringify(value, null, 2);
}

function generateHeaders() {
  return {
    requestId: crypto.randomUUID(),
    requestDateTime: new Date().toISOString()
  };
}

function buildBodyForEndpoint(endpoint, form) {
  if (endpoint === "/api/v1/customer") {
    return "fullName=" + form.fullName.value
        + "&idNumber=" + form.idNumber.value
        + "&phone=" + form.phone.value
        + "&email=" + form.email.value;
  }

  if (endpoint === "/api/v1/customer/face-compare") {
    return "customerCode=" + form.customerCode.value;
  }

  return "";
}

async function signPayload(plainText, secretKey) {
  const encoder = new TextEncoder();

  const key = await crypto.subtle.importKey(
      "raw",
      encoder.encode(secretKey),
      {
        name: "HMAC",
        hash: "SHA-256"
      },
      false,
      ["sign"]
  );

  const signatureBuffer = await crypto.subtle.sign(
      "HMAC",
      key,
      encoder.encode(plainText)
  );

  const bytes = new Uint8Array(signatureBuffer);
  let binary = "";

  bytes.forEach(function (byte) {
    binary += String.fromCharCode(byte);
  });

  return btoa(binary);
}

function clearFaceComparePreview() {
  faceComparePreview.hidden = true;
  idCardPreview.removeAttribute("src");
  selfiePreview.removeAttribute("src");
}

function renderFaceComparePreview(data) {
  if (!data || !data.idCardImageBase64 || !data.selfieImageBase64) {
    clearFaceComparePreview();
    return;
  }

  idCardPreview.src =
      "data:" + (data.idCardImageMimeType || "image/jpeg")
          + ";base64,"
          + data.idCardImageBase64;

  selfiePreview.src =
      "data:" + (data.selfieImageMimeType || "image/jpeg")
          + ";base64,"
          + data.selfieImageBase64;

  faceComparePreview.hidden = false;
}

async function submitForm(event, endpoint) {
  event.preventDefault();

  const form = event.target;
  const formData = new FormData(form);
  const generated = generateHeaders();

  const headers = {
    "Request-ID": generated.requestId,
    "Request-DateTime": generated.requestDateTime
  };

  const bodyText = buildBodyForEndpoint(endpoint, form.elements);
  const plainText =
      bodyText +
      headers["Request-ID"] +
      headers["Request-DateTime"];

  const signature = await signPayload(
      plainText,
      secretKeyInput.value
  );

  headers["JWS-Signature"] = signature;

  if (endpoint === "/api/v1/customer/face-compare") {
    clearFaceComparePreview();
  }

  try {
    prettyLog({
      operation: API_BASE + endpoint,
      status: "sending",
      requestId: headers["Request-ID"],
      requestDateTime: headers["Request-DateTime"],
      signature: signature,
      bodyText: bodyText
    });

    const response = await fetch(
        API_BASE + endpoint,
        {
          method: "POST",
          body: formData,
          headers: headers
        }
    );

    const responseText = await response.text();

    let responseBody;

    try {
      responseBody = JSON.parse(responseText);
    } catch (e) {
      responseBody = responseText;
    }

    prettyLog({
      requestId: headers["Request-ID"],
      status: response.status,
      response: responseBody
    });

    if (endpoint === "/api/v1/customer/face-compare") {
      renderFaceComparePreview(responseBody && responseBody.data);
    }
  } catch (error) {
    prettyLog({
      requestId: headers["Request-ID"],
      error: error.message
    });
  }
}

customerForm.addEventListener(
    "submit",
    function (event) {
      submitForm(event, "/api/v1/customer");
    }
);

faceForm.addEventListener(
    "submit",
    function (event) {
      submitForm(event, "/api/v1/customer/face-compare");
    }
);

livenessForm.addEventListener(
    "submit",
    function (event) {
      submitForm(event, "/api/v1/customer/liveness");
    }
);

clearOutputButton.addEventListener(
    "click",
    function () {
      clearFaceComparePreview();
      prettyLog("Ready to test.");
    }
);

window.addEventListener(
    "load",
    function () {
      clearFaceComparePreview();
      prettyLog("Ready to test.");
    }
);
