document.addEventListener("DOMContentLoaded", function () {
    google.charts.load('current', { packages: ["orgchart"] });

    const stepData = {
        "unloading": {
            title: "Unloading",
            description: "Cargo is unloaded from the vessel using specialized cranes and equipment.",
            contractor: {
                name: "HarborLift GmbH",
                contact: "Lena Schneider",
                email: "lena.schneider@harborlift.de"
            },
            tree: [
                ['Unloading', '', 'Cargo unloading process'],

                // big subtree: cranes, vehicles, teams
                ['Crane Operation', 'Unloading', 'Using cranes'],
                ['Crane 1', 'Crane Operation', 'Main crane at dock'],
                ['Crane 2', 'Crane Operation', 'Auxiliary crane'],
                ['Crane Maintenance', 'Crane Operation', 'Routine check'],

                ['Forklift Operation', 'Unloading', 'Moving pallets'],
                ['Forklift 1', 'Forklift Operation', 'Operator A'],
                ['Forklift 2', 'Forklift Operation', 'Operator B'],

                ['Team A', 'Unloading', 'Primary unloading crew'],
                ['Team Lead', 'Team A', 'Supervisor'],
                ['Worker 1', 'Team A', 'Unloading staff'],
                ['Worker 2', 'Team A', 'Unloading staff'],

                ['Team B', 'Unloading', 'Secondary unloading crew'],
                ['Worker 3', 'Team B', 'Unloading staff'],

                ['Safety Check', 'Unloading', 'Ensuring safety compliance'],
                ['Safety Officer', 'Safety Check', 'Oversees safety procedures'],

                ['Manual Inspection', 'Unloading', 'Visual inspection for damage'],
            ]
        },

        "rfid-scanning": {
            title: "RFID Label Scanning",
            description: "RFID tags are scanned to track each item upon arrival.",
            contractor: {
                name: "Steinweg Logistics",
                contact: "Paul Becker",
                email: "paul.becker@steinweg.de"
            },
            tree: [
                ['RFID Scanning', '', 'Scanning all incoming items'],

                ['Label Check', 'RFID Scanning', 'Check label integrity'],
                ['Label Verification', 'Label Check', 'Verify against manifest'],
                ['Label Cleaning', 'Label Check', 'Clean damaged labels'],

                ['Database Update', 'RFID Scanning', 'Update inventory system'],
                ['Update Inventory', 'Database Update', 'Record quantity and location'],
                ['Sync ERP', 'Database Update', 'Sync with ERP system'],
                ['Generate Reports', 'Database Update', 'Create scanning logs'],

                ['Error Handling', 'RFID Scanning', 'Handle scan errors'],
                ['Retry Scan', 'Error Handling', 'Automatic retry'],
                ['Manual Override', 'Error Handling', 'Human intervention'],

                ['Security Check', 'RFID Scanning', 'Prevent unauthorized scans'],
                ['Access Control', 'Security Check', 'User permissions'],
                ['Audit Logs', 'Security Check', 'Tracking scan activity'],
            ]
        },

        "warehousing": {
            title: "Warehousing",
            description: "Goods are stored temporarily in our secure warehouse facility.",
            contractor: {
                name: "Steinweg Logistics",
                contact: "Anna Keller",
                email: "anna.keller@steinweg.de"
            },
            tree: [
                ['Warehousing', '', 'Storing items safely'],

                ['Inventory Check', 'Warehousing', 'Confirm quantity'],
                ['Cycle Count', 'Inventory Check', 'Periodic counts'],
                ['Spot Check', 'Inventory Check', 'Random checks'],

                ['Storage Allocation', 'Warehousing', 'Assign storage location'],
                ['Zone A', 'Storage Allocation', 'Cold storage'],
                ['Zone B', 'Storage Allocation', 'Dry storage'],

                ['Handling Equipment', 'Warehousing', 'Material handling'],
                ['Pallet Jacks', 'Handling Equipment', 'Moving pallets'],
                ['Conveyor Belts', 'Handling Equipment', 'Automated transport'],
                ['Robotic Arms', 'Handling Equipment', 'Picking and packing'],

                ['Security', 'Warehousing', 'Access control and monitoring'],
                ['CCTV', 'Security', 'Surveillance cameras'],
                ['Access Cards', 'Security', 'Restricted area control'],
                ['Alarm System', 'Security', 'Intruder alerts'],

                ['Maintenance', 'Warehousing', 'Facility upkeep'],
                ['Cleaning', 'Maintenance', 'Scheduled cleaning'],
                ['Repairs', 'Maintenance', 'Equipment repairs'],
            ]
        },

        "disposal": {
            title: "Disposal (if needed)",
            description: "Goods that fail compliance checks are properly disposed of.",
            contractor: {
                name: "Steinweg Logistics",
                contact: "Jan Hoffmann",
                email: "jan.hoffmann@steinweg.de"
            },
            tree: [
                ['Disposal', '', 'Handling non-compliant goods'],

                // small tree here, just few nodes
                ['Compliance Check', 'Disposal', 'Verify regulations'],
                ['Regulatory Body', 'Compliance Check', 'Approval authority'],

                ['Waste Management', 'Disposal', 'Proper disposal methods'],
                ['Recycling', 'Waste Management', 'Recycle reusable materials'],
                ['Incineration', 'Waste Management', 'Dispose hazardous waste'],
            ]
        },

        "customs-clearance": {
            title: "Customs Clearance",
            description: "Required documents are processed to release goods from customs.",
            contractor: {
                name: "Steinweg Logistics",
                contact: "Sophie Fischer",
                email: "sophie.fischer@steinweg.de"
            },
            tree: [
                ['Customs Clearance', '', 'Process customs documents'],

                ['Document Verification', 'Customs Clearance', 'Check paperwork'],
                ['Invoice Check', 'Document Verification', 'Match purchase invoice'],
                ['Certificate Check', 'Document Verification', 'Verify certificates'],
                ['Tariff Classification', 'Document Verification', 'Classify goods'],

                ['Fees Payment', 'Customs Clearance', 'Pay tariffs and fees'],
                ['Calculate Fees', 'Fees Payment', 'Based on classification'],
                ['Payment Processing', 'Fees Payment', 'Payment gateway'],

                ['Release Goods', 'Customs Clearance', 'Final approval'],
                ['Inspection', 'Release Goods', 'Physical check by customs'],
                ['Clearance Confirmation', 'Release Goods', 'Documentation'],

                ['Dispute Handling', 'Customs Clearance', 'If clearance denied'],
                ['Appeals', 'Dispute Handling', 'Legal appeals process'],
                ['Re-Inspection', 'Dispute Handling', 'Second review'],

                ['Logistics Coordination', 'Customs Clearance', 'Organize shipment post-clearance'],
                ['Transport Scheduling', 'Logistics Coordination', 'Trucks, trains'],
                ['Warehouse Notification', 'Logistics Coordination', 'Inform warehousing'],
            ]
        }
    };

    const steps = document.querySelectorAll(".step");
    const stepTitle = document.querySelector(".step-title");
    const stepDescription = document.querySelector("#step-description");
    const contractorDetails = document.querySelector("#contractor-details ul");
    const chartContainer = document.getElementById("step-chart");

    // Function to draw chart
    function drawChart(treeData) {
        if (!treeData || !treeData.length) {
            chartContainer.innerHTML = "";  // clear if no data
            return;
        }

        // Prepare DataTable
        const data = new google.visualization.DataTable();
        data.addColumn('string', 'Name');
        data.addColumn('string', 'Manager');
        data.addColumn('string', 'ToolTip');

        data.addRows(treeData);

        // Create chart
        const chart = new google.visualization.OrgChart(chartContainer);
        chart.draw(data, {
            allowHtml: true,
            nodeClass: 'orgchart-node',
            selectedNodeClass: 'orgchart-node-selected',
            size: 'small'
        });
    }

    // Draw chart only after google charts loaded
    google.charts.setOnLoadCallback(() => {
        // Attach click handlers
        steps.forEach(step => {
            step.addEventListener("click", () => {
                const key = step.dataset.step;
                const data = stepData[key];

                if (data) {
                    stepTitle.textContent = data.title;
                    stepDescription.innerHTML = `<strong>Description:</strong> ${data.description}`;
                    contractorDetails.innerHTML = `
                        <li><strong>Name:</strong> ${data.contractor.name}</li>
                        <li><strong>Contact:</strong> ${data.contractor.contact}</li>
                        <li><strong>Email:</strong> ${data.contractor.email}</li>
                    `;

                    drawChart(data.tree);
                }
            });
        });

        // Optionally: Trigger click on first step to load initial details and chart
        if (steps.length) steps[0].click();
    });
});
