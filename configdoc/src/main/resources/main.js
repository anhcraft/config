(function () {
    const btn = document.getElementById("toggle-example");
    let showExample = false;

    if (window.localStorage.getItem("configdoc-example") === "1") {
        showExample = true;
    }

    function render() {
        Array.from(document.getElementsByClassName("examples")).forEach(function (e) {
            e.style.display = showExample ? "block" : "none";
        });

        if (showExample) {
            btn.classList.add("active")
        } else {
            btn.classList.remove("active")
        }
    }

    btn.onclick = function () {
        showExample = !showExample;
        window.localStorage.setItem("configdoc-example", showExample ? "1" : "0");
        render();
    };

    render();
})()
