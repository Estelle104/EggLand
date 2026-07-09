document.addEventListener("DOMContentLoaded", () => {
  const canvas = document.getElementById("chartMensuel");

  if (!canvas || typeof Chart === "undefined") {
    return;
  }

  const recettes =
    typeof recettesMensuelles !== "undefined" ? recettesMensuelles : {};
  const depenses =
    typeof depensesMensuelles !== "undefined" ? depensesMensuelles : {};
  const labels = Object.keys(recettes);

  const toNumber = (value) => {
    if (value == null) {
      return 0;
    }
    if (typeof value === "number") {
      return value;
    }
    return Number(String(value).replace(/\s/g, "").replace(",", ".")) || 0;
  };

  const toMillions = (value) => Number((toNumber(value) / 1000000).toFixed(2));
  const formatMillions = (value) =>
    value.toLocaleString("fr-MG", {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2,
    });

  const recettesData = labels.map((mois) => toMillions(recettes[mois]));
  const depensesData = labels.map((mois) => toMillions(depenses[mois]));

  new Chart(canvas, {
    type: "bar",
    data: {
      labels,
      datasets: [
        {
          label: "Recettes",
          data: recettesData,
          backgroundColor: "rgba(25, 135, 84, 0.72)",
          borderColor: "rgb(25, 135, 84)",
          borderWidth: 1,
          borderRadius: 6,
          borderSkipped: false,
          maxBarThickness: 34,
        },
        {
          label: "Dépenses",
          data: depensesData,
          backgroundColor: "rgba(220, 53, 69, 0.72)",
          borderColor: "rgb(220, 53, 69)",
          borderWidth: 1,
          borderRadius: 6,
          borderSkipped: false,
          maxBarThickness: 34,
        },
      ],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        intersect: false,
        mode: "index",
      },
      plugins: {
        legend: {
          position: "top",
        },
        tooltip: {
          callbacks: {
            label: (context) =>
              `${context.dataset.label}: ${formatMillions(context.parsed.y)} M Ar`,
          },
        },
      },
      scales: {
        x: {
          grid: {
            display: false,
          },
        },
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => `${formatMillions(value)} M`,
          },
          title: {
            display: true,
            text: "Million d'ariary",
          },
        },
      },
    },
  });
});
