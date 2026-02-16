document.addEventListener("DOMContentLoaded", () => {
    const movieNameElms = Array.from(document.getElementsByClassName("neocinema-header-category-movieName"));
    const genreElms = Array.from(document.getElementsByClassName("neocinema-header-category-genre"));
    const summaryElms = Array.from(document.getElementsByClassName("neocinema-header-category-summary"));
    const categorySelect = document.getElementById("neocinema-header-search-category");
    const searchSubmit = document.getElementById("neocinema-header-search-submit");
    const searchForm = searchSubmit.closest('form');

    // スマホの3点メニューボタン
    const menuBtnElm = document.getElementById("neocinema-header-menu-open-btn");

    genreElms.forEach(elm => {
        elm.addEventListener("mousedown", function (e) {
            if (this.options.length > 5) {
                e.preventDefault();
                this.setAttribute("size", 10);
            }
        });

        elm.addEventListener("click", function (e) {
            if (this.getAttribute("size")) {
                const option = e.target.closest('option');
                if (option && !option.disabled) {
                    this.value = option.value;
                    this.removeAttribute("size");
                    this.blur();
                    updateButtonState();
                }
            }
        });

        elm.addEventListener("change", function () {
            this.removeAttribute("size");
            updateButtonState();
        });

        elm.addEventListener("blur", function () {
            this.removeAttribute("size");
        });
    });

    function updateButtonState() {
        const category = categorySelect.value;
        if (category === "movieName") {
            searchSubmit.disabled = movieNameElms.some(elm => elm.value.trim() === "");
        } else if (category === "genre") {
            searchSubmit.disabled = genreElms.some(elm => elm.value === "");
        } else if (category === "summary") {
            searchSubmit.disabled = summaryElms.some(elm => elm.value.trim() === "");
        }
    }

    categorySelect.addEventListener("change", (event) => {
        const category = event.target.value;
        
        movieNameElms.forEach(elm => {
            elm.hidden = (category !== "movieName");
            elm.disabled = (category !== "movieName");
            if (elm.disabled) elm.value = "";
        });
        genreElms.forEach(elm => {
            elm.hidden = (category !== "genre");
            elm.disabled = (category !== "genre");
            if (elm.disabled) elm.value = "";
        });
        summaryElms.forEach(elm => {
            elm.hidden = (category !== "summary");
            elm.disabled = (category !== "summary");
            if (elm.disabled) elm.value = "";
        });
        
        updateButtonState();
    });

    movieNameElms.forEach(elm => elm.addEventListener("input", updateButtonState));
    genreElms.forEach(elm => elm.addEventListener("change", updateButtonState));
    summaryElms.forEach(elm => elm.addEventListener("input", updateButtonState));

    searchForm.addEventListener("submit", (e) => {
        // ボタンがdisabled（未入力など）の場合は送信を止める
        if (searchSubmit.disabled) {
            e.preventDefault();
        }
    });

    // スマホのメニュー開閉処理
    let open = false;
    menuBtnElm.addEventListener("click", () => {
        const hiddenElements = document.querySelectorAll(".neocinema-header-hidden");
        const topElements = document.querySelectorAll(".neocinema-header-main");
        // const bodyElement = document.querySelector("body");

        hiddenElements.forEach((elm) => {
            elm.classList.toggle("neocinema-header-open");
        });

        if (open) {
            topElements.forEach((elm) => {
                elm.style.height = null;
            });
            // bodyElement.style.paddingTop = "60px";

            open = false;
        } else {
            topElements.forEach((elm) => {
                elm.style.height = "auto";
            });
            // bodyElement.style.paddingTop = "560px";

            open = true;
        }

    });

    // オーバーレイクリックで閉じる
    document.getElementById('neocinema-header-overlay').addEventListener('click', () => {
        // 全ての .neocinema-header-hidden から .neocinema-header-open を外す
        const targets = document.querySelectorAll('.neocinema-header-hidden');
        targets.forEach(target => {
            target.classList.remove('neocinema-header-open');
        });
    });

    // closeボタン
    document.getElementById("neocinema-header-nav-close-btn").addEventListener("click", () => {
        const targets = document.querySelectorAll('.neocinema-header-hidden');
        targets.forEach(target => {
            target.classList.remove('neocinema-header-open');
        });
    });

    document.getElementById("neocinema-header-search-icon").addEventListener("click", () => {
        const targetClassName = "neocinema-header-category-" + categorySelect.value
        console.log(targetClassName);
        const targets = document.querySelectorAll("." + targetClassName);
        console.log(targets.length);
        targets.forEach(target => {
            target.focus();
        });
    });

    setDefaultSearchParam();
});

function setDefaultSearchParam() {
    const params = new URLSearchParams(window.location.search);
    const category = params.get("category") || "movieName";
    const param = params.get("param");

    const catSelect = document.getElementById("neocinema-header-search-category");
    
    catSelect.value = category;
    catSelect.dispatchEvent(new Event('change'));

    if (category === "movieName") {
        const elm = document.getElementsByClassName("neocinema-header-category-movieName")[0];
        if (elm) elm.value = param || "";
    } else if (category === "genre") {
        const elm = document.getElementsByClassName("neocinema-header-category-genre")[0];
        if (elm && param) elm.value = param;
    } else if (category === "summary") {
        const elm = document.getElementsByClassName("neocinema-header-category-summary")[0];
        if (elm) elm.value = param || "";
    }

    const searchSubmit = document.getElementById("neocinema-header-search-submit");
    if (category === "movieName") {
        searchSubmit.disabled = !document.getElementsByClassName("neocinema-header-category-movieName")[0].value.trim();
    } else if (category === "genre") {
        searchSubmit.disabled = !document.getElementsByClassName("neocinema-header-category-genre")[0].value;
    } else if (category === "summary") {
        searchSubmit.disabled = !document.getElementsByClassName("neocinema-header-category-summary")[0].value.trim();
    }
}