const labels = Object.keys(recettesMensuelles);
const recettesData = Object.values(recettesMensuelles).map((v) =>
  parseFloat(v),
);
const depensesData = Object.values(depensesMensuelles).map((v) =>
  parseFloat(v),
);

const ctx = document.getElementById("chartMensuel").getContext("2d");
new Chart(ctx, {
  type: "bar",
  data: {
    labels: labels,
    datasets: [
      {
        label: "Recettes",
        data: recettesData,
        backgroundColor: "rgba(25, 135, 84, 0.7)",
        borderColor: "rgb(25, 135, 84)",
        borderWidth: 1,
        borderRadius: 4,
      },
      {
        label: "Dépenses",
        data: depensesData,
        backgroundColor: "rgba(220, 53, 69, 0.7)",
        borderColor: "rgb(220, 53, 69)",
        borderWidth: 1,
        borderRadius: 4,
      },
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: "top" },
      tooltip: {
        callbacks: {
          label: (ctx) =>
            `${ctx.dataset.label}: ${ctx.raw.toLocaleString("fr-MG")} Ar`,
        },
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => value.toLocaleString("fr-MG") + " Ar",
        },
      },
    },
  },
});