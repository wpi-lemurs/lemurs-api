const getApiHost = () => process.env.REACT_APP_LEMURS_API_HOST || 'https://lemurs-dev.wpi.edu/api';

const delay = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

const mockDaily = [
  { id: 'd1', question: 'Daily: How are you feeling today?', type: 'text' },
];

const mockWeekly = [
  { id: 'w1', question: 'Weekly: What was your biggest win?', type: 'text' },
];

const mockRewards = [
  { id: 'r1', name: 'Coffee Gift Card', points: 100 },
  { id: 'r2', name: 'Movie Ticket', points: 200 },
];

export async function getDailySurvey(token) {
  const response = await fetch(`${getApiHost()}/survey/daily`, {
    headers: { Authorization: token },
  });

  if (response.status === 201) {
    return { status: 201, data: null, message: 'Survey not currently available. Try again within survey available times.' };
  }
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  const data = await response.json();
  return { status: 200, data };
}

export async function getWeeklySurvey(token) {
  const response = await fetch(`${getApiHost()}/survey/weekly`, {
    headers: { Authorization: token },
  });

  if (response.status === 201) {
    return { status: 201, data: null, message: 'Survey not currently available. Try again within survey available times.' };
  }
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  const data = await response.json();
  return { status: 200, data };
}

export async function saveSurveyQuestion(kind, question, token) {
  await delay(150);
  const target = kind === 'weekly' ? mockWeekly : mockDaily;
  if (question.id) {
    const idx = target.findIndex((q) => q.id === question.id);
    if (idx >= 0) target[idx] = { ...target[idx], ...question };
  } else {
    const idPrefix = kind === 'weekly' ? 'w' : 'd';
    target.push({ ...question, id: `${idPrefix}${Date.now()}` });
  }
  return { status: 200, data: target };
}

export async function deleteSurveyQuestion(kind, id, token) {
  await delay(150);
  const target = kind === 'weekly' ? mockWeekly : mockDaily;
  const next = target.filter((q) => q.id !== id);
  if (kind === 'weekly') {
    mockWeekly.length = 0;
    mockWeekly.push(...next);
  } else {
    mockDaily.length = 0;
    mockDaily.push(...next);
  }
  return { status: 200, data: next };
}

export async function getRewards(token) {
  await delay(150);
  return { status: 200, data: mockRewards };
}

export async function saveReward(reward, token) {
  await delay(150);
  if (reward.id) {
    const idx = mockRewards.findIndex((r) => r.id === reward.id);
    if (idx >= 0) mockRewards[idx] = { ...mockRewards[idx], ...reward };
  } else {
    mockRewards.push({ ...reward, id: `r${Date.now()}` });
  }
  return { status: 200, data: mockRewards };
}

export async function deleteReward(id, token) {
  await delay(150);
  const next = mockRewards.filter((r) => r.id !== id);
  mockRewards.length = 0;
  mockRewards.push(...next);
  return { status: 200, data: next };
}
