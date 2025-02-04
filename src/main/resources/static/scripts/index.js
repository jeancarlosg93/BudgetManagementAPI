document.addEventListener("DOMContentLoaded", async () => {
  await loadUsers();
  addButtonEventListener();
});

const loadUsers = async () => {
  const dropdown = document.getElementById("options");
  const authHeader = "Basic " + btoa("admin:admin");
  fetch("http://localhost:8080/api/user/all", {
    method: "GET",
    headers: {
      Authorization: authHeader,
      "Content-Type": "application/json",
      "Accept-Language": "en",
    },
    credentials: "include",
  })
    .then((response) => response.json())

    .then((data) => {
      console.log(data);
      dropdown.innerHTML = "";
      data.forEach((item) => {
        const option = document.createElement("option");
        option.value = item.id;
        option.textContent = `${item.id} - ${item.firstName} ${item.lastName}`;
        dropdown.appendChild(option);
      });
    })
    .catch((error) => console.error("Error fetching data:", error));
};

const addButtonEventListener = () => {
  const button = document.getElementById("load-btn");
  button.addEventListener("click", () => {
    const dropdown = document.getElementById("options");
    const selectedId = dropdown.value;

    if (selectedId) {
      const authHeader = "Basic " + btoa("admin:admin");
      fetch(`http://localhost:8080/api/report/user/${selectedId}`, {
        method: "GET",
        headers: {
          Authorization: authHeader,
          "Content-Type": "application/json",
          "Accept-Language": "en",
        },
        credentials: "include",
      })
        .then((response) => response.json())

        .then((data) => {
          console.log(data);
          dropdown.innerHTML = "";
          if (data.length == 0) {
            const option = document.createElement("option");
            option.disabled = true;
            option.selected = true;
            option.textContent = `No reports found`;
            dropdown.appendChild(option);
          } else {
            data.forEach((item) => {
              const option = document.createElement("option");
              option.value = item.id;
              option.textContent = `${item.id}`;
              dropdown.appendChild(option);
            });
            document.getElementById("instructions").innerText =
              "Please select a report to load";
            const loadBtn = document.createElement("button");
            loadBtn.innerText = "Load Report";
            loadBtn.addEventListener("click", () => {
              loadReport(dropdown.value);
            });
            button.parentNode.replaceChild(loadBtn, button);
          }
        })
        .catch((error) => console.error("Error fetching data:", error));
    } else {
      alert("Please select a user first.");
    }
  });
};

const loadReport = async (id) => {
  const authHeader = "Basic " + btoa("admin:admin");

  fetch(`http://localhost:8080/api/report/${id}`, {
    method: "GET",
    headers: {
      Authorization: authHeader,
      "Content-Type": "application/json",
      "Accept-Language": "en",
    },
    credentials: "include",
  })
    .then((response) => response.json())
    .then((data) => {
      document.getElementById("report").classList.remove("hidden");
      document.getElementById("start-date").textContent = data.startDate;
      document.getElementById("end-date").textContent = data.endDate;
      document.getElementById(
        "total-expenses"
      ).textContent = `$${data.totalExpense.toFixed(2)}`;
      document.getElementById(
        "total-income"
      ).textContent = `$${data.totalIncome.toFixed(2)}`;
      document.getElementById(
        "net-amount"
      ).textContent = `$${data.netAmount.toFixed(2)}`;

      const expensesList = document.getElementById("expenses");
      expensesList.innerHTML = "";
      data.expenses.forEach((expense) => {
        const li = document.createElement("li");
        li.textContent = `${expense.date}: ${
          expense.category.name
        } - $${expense.amount.toFixed(2)} (${expense.description})`;
        expensesList.appendChild(li);
      });

      const incomesList = document.getElementById("incomes");
      incomesList.innerHTML = "";
      data.incomes.forEach((income) => {
        const li = document.createElement("li");
        li.textContent = `${income.date}: ${
          income.type
        } - $${income.amount.toFixed(2)} (${income.description})`;
        incomesList.appendChild(li);
      });
    })
    .catch((error) => console.error("Error fetching report:", error));
};
