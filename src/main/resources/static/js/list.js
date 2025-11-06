$(document).ready(function() {
    $("#filter-form").on("submit", function(event) {
        event.preventDefault();

        const query = $(this).serialize();
        $.getJSON("/api/products?" + query, function(data) {
            const table = $("#item-list-table");
            table.find("tr:gt(0)").remove(); // clear rows except header

            data.forEach(p => {
                table.append(`
                    <tr>
                        <td><a href="/products/${p.id}" class="item-name-link">${p.productName}</a></td>
                        <td>$${p.productPrice.toFixed(2)}</td>
                    </tr>
                `);
            });
        });
    });
});

var itemListTable = document.querySelector('#item-list-table');

itemListTable.addEventListener('click', function(event) {
    if (event.target.matches('a.item-name-link')) {
        event.preventDefault();
        var itemId = event.target.getAttribute('href').split('/')[2];
        window.location.href = '/products/' + itemId;
    }
});