document.querySelectorAll(".menu-container").forEach(menu => {

    const btn = menu.querySelector(".menu");
    const dropdown = menu.querySelector(".menu-dropdown");

    btn.addEventListener("click", (e) => {
        e.stopPropagation();
        dropdown.classList.toggle("active");
    });

});

document.addEventListener("click", () => {
    document.querySelectorAll(".menu-dropdown").forEach(d => {
        d.classList.remove("active");
    });
});