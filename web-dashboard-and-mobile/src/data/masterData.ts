import { LineMasterItem, UserAccount, LineTargetConfig, IncidentRecord, ProductionEntryRecord, QualityEntryRecord } from '../types';

export const ALL_FACTORY_LINES: LineMasterItem[] = [
  // Sew 1 (22 lines)
  { id: 'A1', floor: 'Sew 1', isActive: true },
  { id: 'A2', floor: 'Sew 1', isActive: true },
  { id: 'B', floor: 'Sew 1', isActive: true },
  { id: 'C', floor: 'Sew 1', isActive: true },
  { id: 'D1A', floor: 'Sew 1', isActive: true },
  { id: 'D2A', floor: 'Sew 1', isActive: true },
  { id: 'D1B', floor: 'Sew 1', isActive: true },
  { id: 'D2B', floor: 'Sew 1', isActive: true },
  { id: 'E1A', floor: 'Sew 1', isActive: true },
  { id: 'E2A', floor: 'Sew 1', isActive: true },
  { id: 'E1B', floor: 'Sew 1', isActive: true },
  { id: 'E2B', floor: 'Sew 1', isActive: true },
  { id: 'FA', floor: 'Sew 1', isActive: true },
  { id: 'FB', floor: 'Sew 1', isActive: true },
  { id: 'G1A', floor: 'Sew 1', isActive: true },
  { id: 'G2A', floor: 'Sew 1', isActive: true },
  { id: 'G1B', floor: 'Sew 1', isActive: true },
  { id: 'G2B', floor: 'Sew 1', isActive: true },
  { id: 'H1A', floor: 'Sew 1', isActive: true },
  { id: 'H2A', floor: 'Sew 1', isActive: true },
  { id: 'H1B', floor: 'Sew 1', isActive: true },
  { id: 'H2B', floor: 'Sew 1', isActive: true },

  // Sew 2 (14 lines)
  { id: 'J1', floor: 'Sew 2', aliases: ['J'], isActive: true },
  { id: 'K1', floor: 'Sew 2', isActive: true },
  { id: 'L1', floor: 'Sew 2', isActive: true },
  { id: 'L2', floor: 'Sew 2', isActive: true },
  { id: 'M1', floor: 'Sew 2', aliases: ['M'], isActive: true },
  { id: 'M2', floor: 'Sew 2', isActive: true },
  { id: 'N1', floor: 'Sew 2', isActive: true },
  { id: 'N2', floor: 'Sew 2', isActive: true },
  { id: 'O1', floor: 'Sew 2', isActive: true },
  { id: 'O2', floor: 'Sew 2', isActive: true },
  { id: 'P1', floor: 'Sew 2', aliases: ['P'], isActive: true },
  { id: 'Q1', floor: 'Sew 2', isActive: true },
  { id: 'Q2', floor: 'Sew 2', isActive: true },
  { id: 'R1', floor: 'Sew 2', aliases: ['R'], isActive: true },

  // Sew 3 (24 lines)
  { id: 'S1', floor: 'Sew 3', isActive: true },
  { id: 'S2', floor: 'Sew 3', isActive: true },
  { id: 'T1', floor: 'Sew 3', isActive: true },
  { id: 'T2', floor: 'Sew 3', isActive: true },
  { id: 'U', floor: 'Sew 3', isActive: true },
  { id: 'UA', floor: 'Sew 3', isActive: true },
  { id: 'UB', floor: 'Sew 3', isActive: true },
  { id: 'V1A', floor: 'Sew 3', isActive: true },
  { id: 'V1B', floor: 'Sew 3', isActive: true },
  { id: 'V2A', floor: 'Sew 3', isActive: true },
  { id: 'V2B', floor: 'Sew 3', isActive: true },
  { id: 'W1A', floor: 'Sew 3', isActive: true },
  { id: 'W1B', floor: 'Sew 3', isActive: true },
  { id: 'W2A', floor: 'Sew 3', isActive: true },
  { id: 'W2B', floor: 'Sew 3', isActive: true },
  { id: 'X1', floor: 'Sew 3', isActive: true },
  { id: 'Y1', floor: 'Sew 3', isActive: true },
  { id: 'Y2', floor: 'Sew 3', isActive: true },
  { id: 'Z1', floor: 'Sew 3', isActive: true },
  { id: 'Z2', floor: 'Sew 3', isActive: true },
  { id: 'ZA1A', floor: 'Sew 3', isActive: true },
  { id: 'ZA1B', floor: 'Sew 3', isActive: true },
  { id: 'ZA2A', floor: 'Sew 3', isActive: true },
  { id: 'ZA2B', floor: 'Sew 3', isActive: true }
];

export const STANDARD_USERS: UserAccount[] = [
  { id: 'u-1', name: 'Kabir Ahmed', username: 'admin', role: 'ADMIN', floor: 'All' },
  { id: 'u-2', name: 'Md. Rafiqul', username: 'prod_reporter', role: 'PRODUCTION_REPORTER', floor: 'Sew 2', assignedLine: 'K1' },
  { id: 'u-3', name: 'Nasrin Sultana', username: 'qa_reporter', role: 'QUALITY_REPORTER', floor: 'Sew 2', assignedLine: 'K1' },
  { id: 'u-4', name: 'Tariq Hasan', username: 'supervisor', role: 'SUPERVISOR', floor: 'Sew 2' },
  { id: 'u-5', name: 'Mahbub Alam', username: 'prod_mgr', role: 'PRODUCTION_MANAGER', floor: 'All' },
  { id: 'u-6', name: 'Shirin Akter', username: 'qa_mgr', role: 'QUALITY_MANAGER', floor: 'All' },
  { id: 'u-7', name: 'Jahangir Hossain', username: 'mechanic', role: 'MECHANIC', floor: 'Sew 2' },
  { id: 'u-8', name: 'Salim Reza', username: 'ie_officer', role: 'IE', floor: 'All' },
  { id: 'u-9', name: 'Biplob Das', username: 'input_coord', role: 'INPUT_MATERIAL', floor: 'Sew 2' },
  { id: 'u-10', name: 'Executive TV', username: 'management', role: 'MANAGEMENT_VIEW', floor: 'All' }
];

export const STANDARD_STYLES = [
  { styleNumber: 'NFL-204', buyer: 'Nike / NFL', name: 'Jacket A', smv: 14.50 },
  { styleNumber: 'NFL-108', buyer: 'Nike / NFL', name: 'Vest A', smv: 12.20 },
  { styleNumber: 'NFL-305', buyer: 'Adidas / NFL', name: 'Jacket B', smv: 16.80 },
  { styleNumber: 'NFL-512', buyer: 'Under Armour', name: 'Vest B', smv: 11.00 },
  { styleNumber: 'NFL-990', buyer: 'Puma', name: 'Hoodie Pro', smv: 13.40 }
];

export const STANDARD_HOUR_SLOTS = [
  '08:00 - 09:00',
  '09:00 - 10:00',
  '10:00 - 11:00',
  '11:00 - 12:00',
  '12:00 - 13:00',
  '14:00 - 15:00',
  '15:00 - 16:00',
  '16:00 - 17:00'
];

export const STANDARD_DEFECT_TYPES = [
  'Poor iron',
  'Uncut thread',
  'Dirty spot',
  'Oil spot',
  'Skip stitch',
  'Open seam',
  'Broken stitch',
  'Puckering',
  'Pleat',
  'Shading'
];

/**
 * Creates realistic synthetic demo data across all three floors, matching Image 1 & Image 2 numbers exactly.
 */
export function generateSyntheticDemoDataset(): {
  targets: LineTargetConfig[];
  productions: ProductionEntryRecord[];
  qualities: QualityEntryRecord[];
  incidents: IncidentRecord[];
} {
  const currentDate = '2026-10-09';

  // Sew 2 exact lines from visual references (Image 1 & Image 2)
  const sew2LinesData: Array<{
    id: string;
    style: string;
    target: number;
    output: number | null; // N1 is targeted but Not Reported!
    eff: number;
    oql: number;
    dhu: number;
  }> = [
    { id: 'J1', style: 'Jacket A', target: 500, output: 450, eff: 70.0, oql: 3.5, dhu: 1.76 },
    { id: 'K1', style: 'Vest A', target: 400, output: 340, eff: 66.0, oql: 4.0, dhu: 1.48 },
    { id: 'L1', style: 'Jacket B', target: 350, output: 280, eff: 64.0, oql: 4.2, dhu: 1.30 },
    { id: 'N1', style: 'Vest B', target: 450, output: null, eff: 0, oql: 0, dhu: 0 }, // Not reported!
    { id: 'N2', style: 'Jacket A', target: 620, output: 560, eff: 76.0, oql: 3.7, dhu: 1.97 },
    { id: 'O1', style: 'Vest A', target: 520, output: 480, eff: 72.0, oql: 4.1, dhu: 1.90 },
    { id: 'P1', style: 'Jacket B', target: 480, output: 420, eff: 68.0, oql: 4.5, dhu: 1.48 },
    { id: 'Q1', style: 'Vest B', target: 600, output: 520, eff: 71.0, oql: 3.9, dhu: 1.32 },
    { id: 'R1', style: 'Hoodie Pro', target: 380, output: 320, eff: 66.0, oql: 4.2, dhu: 1.25 }
  ];

  // Sew 1 targeted lines
  const sew1LinesData = [
    { id: 'A1', style: 'Jacket A', target: 460, output: 430, eff: 78.0, oql: 3.1, dhu: 1.4 },
    { id: 'B', style: 'Vest A', target: 500, output: 480, eff: 80.0, oql: 2.9, dhu: 1.2 },
    { id: 'C', style: 'Jacket B', target: 420, output: 390, eff: 74.0, oql: 3.4, dhu: 1.5 },
    { id: 'D1A', style: 'Vest B', target: 480, output: 440, eff: 75.0, oql: 3.8, dhu: 1.6 },
    { id: 'E1A', style: 'Hoodie Pro', target: 400, output: 370, eff: 73.0, oql: 3.6, dhu: 1.4 }
  ];

  // Sew 3 targeted lines
  const sew3LinesData = [
    { id: 'S1', style: 'Jacket A', target: 520, output: 490, eff: 72.0, oql: 3.6, dhu: 1.5 },
    { id: 'T1', style: 'Vest A', target: 480, output: 450, eff: 71.0, oql: 3.9, dhu: 1.7 },
    { id: 'U', style: 'Jacket B', target: 440, output: 410, eff: 69.0, oql: 4.0, dhu: 1.8 },
    { id: 'V1A', style: 'Vest B', target: 500, output: 470, eff: 73.0, oql: 3.5, dhu: 1.4 }
  ];

  const targets: LineTargetConfig[] = [];
  const productions: ProductionEntryRecord[] = [];
  const qualities: QualityEntryRecord[] = [];

  // Helper to push line data
  const populate = (lines: typeof sew2LinesData, floor: 'Sew 1' | 'Sew 2' | 'Sew 3') => {
    lines.forEach((item, index) => {
      targets.push({
        lineId: item.id,
        floor,
        date: currentDate,
        dailyTargetPcs: item.target,
        hourlyTargetPcs: Math.round(item.target / 8),
        buyer: 'Nike / NFL',
        styleNumber: item.style,
        smv: 14.5,
        operatorsCount: 20,
        machinesCount: 20,
        helpersCount: 2,
        includeHelpersInEfficiency: false,
        workingHoursSoFar: 7,
        totalShiftHours: 8,
        isDemo: true
      });

      if (item.output !== null) {
        productions.push({
          id: `demo-p-${floor}-${item.id}`,
          submissionId: `sub-p-${floor}-${item.id}`,
          lineId: item.id,
          floor,
          date: currentDate,
          hourSlot: '15:00 - 16:00',
          styleNumber: item.style,
          outputQty: item.output,
          isConfirmedZero: item.output === 0,
          remarks: 'Standard production flow',
          reportedBy: 'Md. Rafiqul (Reporter)',
          timestamp: Date.now() - (index + 1) * 360000,
          revision: 1,
          isDemo: true
        });

        // Generate quality sample inspection
        const checked = 100;
        const defCount = Math.round(item.dhu * (checked / 100) * 10);
        const defective = Math.min(checked, Math.max(1, Math.round(defCount * 0.8)));

        qualities.push({
          id: `demo-q-${floor}-${item.id}`,
          submissionId: `sub-q-${floor}-${item.id}`,
          lineId: item.id,
          floor,
          date: currentDate,
          hourSlot: '15:00 - 16:00',
          styleNumber: item.style,
          checkedGarments: checked,
          defectiveGarments: defective,
          defects: [
            { defectType: 'Poor iron', count: Math.ceil(defCount * 0.45) },
            { defectType: 'Uncut thread', count: Math.ceil(defCount * 0.3) },
            { defectType: 'Dirty spot', count: Math.floor(defCount * 0.15) },
            { defectType: 'Skip stitch', count: Math.max(1, Math.floor(defCount * 0.1)) }
          ],
          totalDefects: defCount,
          isConfirmedZero: false,
          remarks: 'Daily in-line inspection',
          reportedBy: 'Nasrin Sultana (QA)',
          timestamp: Date.now() - (index + 1) * 360000,
          revision: 1,
          isDemo: true
        });
      }
    });
  };

  populate(sew1LinesData as any, 'Sew 1');
  populate(sew2LinesData, 'Sew 2');
  populate(sew3LinesData as any, 'Sew 3');

  // Active Incidents with live elapsed timer (survives reload!)
  const now = Date.now();
  const incidents: IncidentRecord[] = [
    {
      id: 'demo-inc-1',
      floor: 'Sew 2',
      lineId: 'K1',
      category: 'MACHINE_BREAKDOWN',
      machineRef: 'SNLS-04',
      description: 'Motor overheated and needle bar vibrating severely',
      reportedCause: 'Bearing worn out in drive belt',
      impactLevel: 'HIGH',
      affectedManpower: 6,
      startTimeMillis: now - 22 * 60 * 1000, // 22 mins ago
      acknowledgedTimeMillis: now - 15 * 60 * 1000,
      acknowledgedBy: 'Jahangir (Mechanic)',
      status: 'ACKNOWLEDGED',
      reportedBy: 'Tariq Hasan (Supervisor)',
      isDemo: true
    },
    {
      id: 'demo-inc-2',
      floor: 'Sew 2',
      lineId: 'L2',
      category: 'QUALITY_PROBLEM',
      machineRef: 'Overlock-02',
      description: 'Recurring puckering along side seam on Jacket B',
      reportedCause: 'Thread tension miscalibrated on needle 2',
      impactLevel: 'MEDIUM',
      affectedManpower: 4,
      startTimeMillis: now - 11 * 60 * 1000, // 11 mins ago
      status: 'OPEN',
      reportedBy: 'Nasrin Sultana (QA)',
      isDemo: true
    },
    {
      id: 'demo-inc-3',
      floor: 'Sew 2',
      lineId: 'N1',
      category: 'LOW_PRODUCTION',
      description: 'Output gap > 25% behind target due for slot 15:00-16:00',
      reportedCause: 'Waiting for collar rib input from cutting section',
      impactLevel: 'HIGH',
      affectedManpower: 20,
      startTimeMillis: now - 35 * 60 * 1000, // 35 mins ago
      status: 'OPEN',
      reportedBy: 'System Low-Production Monitor',
      isDemo: true
    }
  ];

  return { targets, productions, qualities, incidents };
}
