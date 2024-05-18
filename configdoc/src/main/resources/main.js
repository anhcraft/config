(function () {
    document.querySelector("#collapse-menu").onclick = function () {
        document.querySelector("#page-layout").classList.toggle("collapsed");
        document.querySelector("#toolbar > *:not(#collapse-menu)").classList.toggle("hidden");
    };

    document.querySelectorAll('.property-tab-nav > div').forEach(tab => {
        tab.onclick = function() {
            if (!this.classList.contains('active')) {
                const container = this.parentElement.parentElement;
                container.querySelectorAll('.property-tab-nav > div').forEach(t => t.classList.remove('active'));
                this.classList.add('active');
                const tabId = this.getAttribute('data-tab-id');
                container.querySelectorAll('.property-tab-content > div').forEach(content => content.classList.remove('active'));
                container.querySelector(`.property-tab-content > div[data-tab-id="${tabId}"]`).classList.add('active');
            }
        };
    });

    document.querySelector("#search-input").oninput = function() {
        if (this.value.length === 0) {
            document.querySelector("#search-view").style.display = "none";
            document.querySelector("#content-view").style.display = "block";
            return;
        }
        document.querySelector("#search-view").style.display = "block";
        document.querySelector("#content-view").style.display = "none";

        const searchResult = search(this.value);
        document.querySelector("#search-results-count").innerText = searchResult.schemas.length;
        document.querySelector("#search-term").innerText = this.value;
        document.querySelector("#search-keywords").innerText = searchResult.keywords.join(", ");
        document.querySelector("#search-results").replaceChildren(
            ...searchResult.schemas.map(schema => {
                const div = document.createElement("div");
                div.innerHTML = `<a href="${schema.path}">${schema.name}</a>`;
                return div;
            })
        )
    }
})()