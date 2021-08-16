if (typeof xhr === "undefined") xhr = new XMLHttpRequest();

getDefaultAttributes();

function getDefaultAttributes() {
    const genre = document.querySelector("input[name='templateGenre']:checked").value;
    xhr.open("GET", "http://localhost:8000/template/default-attr-map?genre=" + genre);

    xhr.onreadystatechange = () => {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            promptAttributes(JSON.parse(xhr.response).attrMap);
        } else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("Invalid game genre.")
        }
    }

    xhr.send();
}

function createTemplate() {
	xhr.open("POST", "http://localhost:8000/template/create");

	xhr.onreadystatechange = () => {
		if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 201) {
		    alert("Successfully created template");
            window.location = "http://localhost:8080/pages/templates.html";
		} else if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 400) {
            alert("There's an invalid attribute or value");
		}
	}

    const attrMap = getAttrMap();

	xhr.send(JSON.stringify({
		attrMap
	}));
}

function promptAttributes(map) {
    document.getElementsByTagName("input")[0].value = map.title;
    delete map.title;

    for (const [key, value] of Object.entries(map)) {
        addOption(key, value);
    }

    restrictOptions();
    document.getElementById("isMultipleChoice").onclick = restrictOptions;
}

function addOption(label, checked) {
    const labelEl = document.createElement("label");
    labelEl.innerHTML = label;
    const input = document.createElement("input");
    input.type = "checkbox";
    input.setAttribute("id", label);
    input.checked = checked === "true";

    labelEl.prepend(input);

    document.getElementById("form").insertBefore(labelEl, document.getElementsByTagName("button")[0]);
}

function restrictOptions() {
    if (!document.getElementById("isMultipleChoice").checked) {
        document.querySelectorAll("input[type='checkbox']:not(#isMultipleChoice)").forEach(el => {
            el.checked = false;
            el.disabled = true;
        });
    } else {
        document.querySelectorAll("input[type='checkbox']:not(#isMultipleChoice)").forEach(el => el.disabled = false);
    }
}

function getAttrMap() {
    const inputs = Array.from(document.getElementsByTagName("input"));

    let attrMap = {
       title: inputs.shift().value
    };

    for (const el of inputs) {
        attrMap[el.getAttribute("id")] = el.checked.toString();
    }

    return attrMap;
}

function capitalize(text) {
    return text[0].toUpperCase() + text.substring(1).toLowerCase();
}