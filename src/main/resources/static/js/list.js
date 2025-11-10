$(document).ready(function() {
    // === FILTER FORM HANDLER ===
    $("#filter-form").on("submit", function(event) {
        event.preventDefault();

        const query = $(this).serialize();
        $.getJSON("/api/products?" + query, function(data) {
            const table = $("#item-list-table");
            table.find("tr:gt(0)").remove(); // clear rows except header

            data.forEach(p => {
                table.append(`
                    <tr data-id="${p.id}">
                        <td><a href="/products/view/${p.id}" class="item-name-link">${p.productName}</a></td>
                        <td>$${p.productPrice.toFixed(2)}</td>
                    </tr>
                `);
            });
        });
    });

    // === CLICK HANDLER FOR PRODUCT LINKS ===
    $("#item-list-table").on("click", "a.item-name-link", function(event) {
        event.preventDefault();
        const href = $(this).attr("href");
        window.location.href = href;
    });

    // === WEBSOCKET CONNECTION ===
    const protocol = window.location.protocol === "https:" ? "wss" : "ws";
    const socketUrl = `${protocol}://${window.location.host}/ws`;
    const socket = new WebSocket(socketUrl);

    socket.addEventListener("open", () => {
        console.log("✅ Connected to WebSocket:", socketUrl);
    });

    socket.addEventListener("close", () => {
        console.log("❌ WebSocket closed");
    });

    socket.addEventListener("message", (event) => {
        console.log("📨 WebSocket message:", event.data);
        try {
            const msg = JSON.parse(event.data);

            if (msg.type === "updatePrice") {
                // Find row for matching item
                const row = $(`#item-list-table tr[data-id='${msg.itemId}']`);
                if (row.length) {
                    row.find("td:last").text(`$${parseFloat(msg.price).toFixed(2)}`);
                } else {
                    console.log("ℹ️ Item not currently displayed, ignoring update");
                }
            }

            if (msg.type === "newProduct") {
                // Append new product to table
                $("#item-list-table").append(`
                    <tr data-id="${msg.id}">
                        <td><a href="/products/view/${msg.id}" class="item-name-link">${msg.name}</a></td>
                        <td>$${parseFloat(msg.price).toFixed(2)}</td>
                    </tr>
                `);
            }
        } catch (err) {
            console.error("❌ Failed to parse message:", event.data, err);
        }
    });
});
