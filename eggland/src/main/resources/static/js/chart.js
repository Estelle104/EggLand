document.addEventListener("DOMContentLoaded", () => {
    const canvas = document.getElementById("productionChart");
    const donnees = window.productionChartData;

    if (!canvas || !donnees || typeof Chart === "undefined") {
        return;
    }

    new Chart(canvas, {
        type: "bar",
        data: {
            labels: donnees.labels,
            datasets: [{
                label: "Œufs produits",
                data: donnees.quantites,
                backgroundColor: "rgba(184, 122, 42, 0.72)",
                borderColor: "#8A4E28",
                borderWidth: 1,
                borderRadius: 7,
                borderSkipped: false,
                maxBarThickness: 46
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: "index"
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: contexte => `${contexte.parsed.y} œuf(s)`
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        color: "#7C6758"
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: "rgba(228, 215, 199, 0.7)"
                    },
                    ticks: {
                        color: "#7C6758",
                        precision: 0
                    },
                    title: {
                        display: true,
                        text: "Nombre d'œufs",
                        color: "#7C6758"
                    }
                }
            }
        }
    });
});
