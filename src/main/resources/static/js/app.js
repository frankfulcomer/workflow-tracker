const itemsBody = document.getElementById('items-body');
const createForm = document.getElementById('create-form');
const createError = document.getElementById('create-error');
const searchBox = document.getElementById('search-box');
const statusFilter = document.getElementById('status-filter');

const STATUSES = ['NEW', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'];

let latestRequestId = 0;

async function fetchItems() {
    const requestId = ++latestRequestId;
    const params = new URLSearchParams();
    if (statusFilter.value) params.set('status', statusFilter.value);
    if (searchBox.value.trim()) params.set('q', searchBox.value.trim());

    const res = await fetch(`/api/items?${params.toString()}`);
    const items = await res.json();

    // Discard this response if a newer fetchItems() call has since been made -
    // otherwise a slow, now-stale request (e.g. the unfiltered initial load)
    // can resolve after a subsequent filter/search and clobber its result.
    if (requestId !== latestRequestId) return;
    renderItems(items);
}

function renderItems(items) {
    itemsBody.innerHTML = '';
    for (const item of items) {
        const tr = document.createElement('tr');
        tr.setAttribute('data-testid', `item-row-${item.id}`);

        tr.innerHTML = `
            <td>${escapeHtml(item.title)}</td>
            <td>${escapeHtml(item.assignee || '')}</td>
            <td><span class="badge ${item.status}" data-testid="item-status-${item.id}">${item.status.replace('_', ' ')}</span></td>
            <td>${formatDate(item.updatedDate)}</td>
            <td class="row-actions"></td>
        `;

        const actionsCell = tr.querySelector('.row-actions');

        const select = document.createElement('select');
        select.setAttribute('data-testid', `status-select-${item.id}`);
        for (const s of STATUSES) {
            const opt = document.createElement('option');
            opt.value = s;
            opt.textContent = s.replace('_', ' ');
            if (s === item.status) opt.selected = true;
            select.appendChild(opt);
        }
        select.addEventListener('change', () => updateStatus(item.id, select.value));
        actionsCell.appendChild(select);

        const delBtn = document.createElement('button');
        delBtn.textContent = 'Delete';
        delBtn.className = 'secondary';
        delBtn.setAttribute('data-testid', `delete-btn-${item.id}`);
        delBtn.addEventListener('click', () => deleteItem(item.id));
        actionsCell.appendChild(delBtn);

        itemsBody.appendChild(tr);
    }
}

async function updateStatus(id, status) {
    const res = await fetch(`/api/items/${id}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status })
    });
    if (!res.ok) {
        const body = await res.json().catch(() => ({}));
        alert(body.error || 'Could not update status');
    }
    fetchItems();
}

async function deleteItem(id) {
    await fetch(`/api/items/${id}`, { method: 'DELETE' });
    fetchItems();
}

createForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    createError.textContent = '';

    const payload = {
        title: document.getElementById('title').value.trim(),
        assignee: document.getElementById('assignee').value.trim(),
        description: document.getElementById('description').value.trim()
    };

    const res = await fetch('/api/items', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        createForm.reset();
        fetchItems();
    } else {
        createError.textContent = 'Could not create item - check the title.';
    }
});

searchBox.addEventListener('input', debounce(fetchItems, 300));
statusFilter.addEventListener('change', fetchItems);

function debounce(fn, delay) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => fn(...args), delay);
    };
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function formatDate(iso) {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleString();
}

fetchItems();
